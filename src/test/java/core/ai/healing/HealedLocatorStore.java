package core.ai.healing;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import core.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class HealedLocatorStore {

    private static final Logger logger =
            LoggerUtil.getLogger(HealedLocatorStore.class);

    private static final String STORE_PATH_PROPERTY =
            "ai.healing.store.path";

    private static final Path DEFAULT_STORE_PATH =
            Path.of(
                    ".healing-store",
                    "healed-locators.json"
            );

    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper()
                    .enable(
                            SerializationFeature.INDENT_OUTPUT
                    );

    private static final Map<String, HealedLocatorRecord> CACHE =
            new ConcurrentHashMap<>();

    private static final Object FILE_LOCK =
            new Object();

    static {
        reload();
    }

    private HealedLocatorStore() {
    }

    /**
     * Stores a successfully healed locator.
     *
     * Existing callers remain compatible with this method.
     */
    public static void store(
            String brokenLocator,
            HealedLocatorCandidate candidate,
            String screen,
            String test) {

        String key =
                requireText(
                        brokenLocator,
                        "Broken locator"
                );

        if (candidate == null) {
            throw new NullPointerException(
                    "Healed locator candidate must not be null"
            );
        }

        Instant now =
                Instant.now();

        HealedLocatorRecord existing =
                CACHE.get(key);

        HealedLocatorRecord record;

        if (existing == null) {

            record =
                    HealedLocatorRecord.builder()
                            .brokenLocator(key)
                            .candidate(candidate)
                            .screen(screen)
                            .testName(test)
                            .successCount(1)
                            .failureCount(0)
                            .createdAt(now)
                            .lastUsedAt(now)
                            .build();

        } else {

            record =
                    HealedLocatorRecord.builder()
                            .brokenLocator(key)
                            .candidate(candidate)
                            .screen(
                                    firstNonBlank(
                                            screen,
                                            existing.getScreen()
                                    )
                            )
                            .testName(
                                    firstNonBlank(
                                            test,
                                            existing.getTestName()
                                    )
                            )
                            .successCount(
                                    existing.getSuccessCount() + 1
                            )
                            .failureCount(
                                    existing.getFailureCount()
                            )
                            .createdAt(
                                    existing.getCreatedAt()
                            )
                            .lastUsedAt(now)
                            .build();
        }

        CACHE.put(
                key,
                record
        );

        persist();
    }

    /**
     * Returns the currently learned candidate.
     */
    public static HealedLocatorCandidate getCached(
            String brokenLocator) {

        HealedLocatorRecord record =
                getRecord(brokenLocator);

        return record == null
                ? null
                : record.getCandidate();
    }

    /**
     * Returns the full persistent learning record.
     */
    public static HealedLocatorRecord getRecord(
            String brokenLocator) {

        if (brokenLocator == null
                || brokenLocator.isBlank()) {
            return null;
        }

        return CACHE.get(
                brokenLocator.trim()
        );
    }

    /**
     * Records a successful cached-locator reuse.
     */
    public static void recordSuccess(
            String brokenLocator) {

        String key =
                requireText(
                        brokenLocator,
                        "Broken locator"
                );

        HealedLocatorRecord existing =
                CACHE.get(key);

        if (existing == null) {
            return;
        }

        CACHE.put(
                key,
                existing.recordSuccess(
                        Instant.now()
                )
        );

        persist();
    }

    /**
     * Records a failed cached-locator reuse.
     */
    public static void recordFailure(
            String brokenLocator) {

        String key =
                requireText(
                        brokenLocator,
                        "Broken locator"
                );

        HealedLocatorRecord existing =
                CACHE.get(key);

        if (existing == null) {
            return;
        }

        CACHE.put(
                key,
                existing.recordFailure(
                        Instant.now()
                )
        );

        persist();
    }

    public static boolean has(
            String brokenLocator) {

        if (brokenLocator == null
                || brokenLocator.isBlank()) {
            return false;
        }

        return CACHE.containsKey(
                brokenLocator.trim()
        );
    }

    public static void remove(
            String brokenLocator) {

        if (brokenLocator == null
                || brokenLocator.isBlank()) {
            return;
        }

        HealedLocatorRecord removed =
                CACHE.remove(
                        brokenLocator.trim()
                );

        if (removed != null) {
            persist();
        }
    }

    /**
     * Reloads the in-memory cache from disk.
     */
    public static void reload() {

        synchronized (FILE_LOCK) {

            CACHE.clear();

            Path storePath =
                    resolveStorePath();

            if (!Files.exists(storePath)) {
                return;
            }

            try {
                Map<String, PersistedRecord> persisted =
                        OBJECT_MAPPER.readValue(
                                storePath.toFile(),
                                new TypeReference<
                                        Map<String, PersistedRecord>>() {
                                }
                        );

                if (persisted == null) {
                    return;
                }

                persisted.forEach(
                        (key, value) -> {

                            HealedLocatorRecord record =
                                    toDomainRecord(
                                            key,
                                            value
                                    );

                            if (record != null) {
                                CACHE.put(
                                        record.getBrokenLocator(),
                                        record
                                );
                            }
                        }
                );

                logger.info(
                        "[Healing Store] Loaded {} healed locator record(s) from {}",
                        CACHE.size(),
                        storePath
                );

            } catch (Exception exception) {

                CACHE.clear();

                logger.warn(
                        "[Healing Store] Could not load {}: {}",
                        storePath,
                        exception.getMessage()
                );
            }
        }
    }

    /**
     * Clears both memory and persisted storage.
     */
    public static void clear() {

        synchronized (FILE_LOCK) {

            CACHE.clear();

            Path storePath =
                    resolveStorePath();

            try {
                Files.deleteIfExists(
                        storePath
                );

                Path parent =
                        storePath.getParent();

                if (parent != null
                        && Files.exists(parent)
                        && isDirectoryEmpty(parent)) {

                    Files.deleteIfExists(parent);
                }

            } catch (IOException exception) {

                logger.warn(
                        "[Healing Store] Could not delete store {}: {}",
                        storePath,
                        exception.getMessage()
                );
            }
        }
    }

    public static int size() {
        return CACHE.size();
    }

    private static void persist() {

        synchronized (FILE_LOCK) {

            Path storePath =
                    resolveStorePath();

            Path parent =
                    storePath.getParent();

            Path temporaryPath =
                    storePath.resolveSibling(
                            storePath.getFileName()
                                    + ".tmp"
                    );

            try {
                if (parent != null) {
                    Files.createDirectories(parent);
                }

                Map<String, PersistedRecord> persisted =
                        new LinkedHashMap<>();

                CACHE.forEach(
                        (key, record) ->
                                persisted.put(
                                        key,
                                        fromDomainRecord(record)
                                )
                );

                OBJECT_MAPPER.writeValue(
                        temporaryPath.toFile(),
                        persisted
                );

                moveAtomically(
                        temporaryPath,
                        storePath
                );

                logger.debug(
                        "[Healing Store] Persisted {} record(s) to {}",
                        persisted.size(),
                        storePath
                );

            } catch (IOException exception) {

                logger.warn(
                        "[Healing Store] Could not persist store {}: {}",
                        storePath,
                        exception.getMessage()
                );

                try {
                    Files.deleteIfExists(
                            temporaryPath
                    );
                } catch (IOException cleanupException) {
                    logger.debug(
                            "[Healing Store] Could not remove temporary file: {}",
                            cleanupException.getMessage()
                    );
                }
            }
        }
    }

    private static PersistedRecord fromDomainRecord(
            HealedLocatorRecord record) {

        PersistedRecord persisted =
                new PersistedRecord();

        persisted.brokenLocator =
                record.getBrokenLocator();

        persisted.locatorType =
                record.getCandidate()
                        .getLocatorType();

        persisted.locatorValue =
                record.getCandidate()
                        .getLocatorValue();

        persisted.confidence =
                record.getCandidate()
                        .getConfidence();

        persisted.explanation =
                record.getCandidate()
                        .getExplanation();

        persisted.screen =
                record.getScreen();

        persisted.testName =
                record.getTestName();

        persisted.successCount =
                record.getSuccessCount();

        persisted.failureCount =
                record.getFailureCount();

        persisted.createdAt =
                record.getCreatedAt()
                        .toString();

        persisted.lastUsedAt =
                record.getLastUsedAt()
                        .toString();

        return persisted;
    }

    private static HealedLocatorRecord toDomainRecord(
            String mapKey,
            PersistedRecord persisted) {

        if (persisted == null) {
            return null;
        }

        try {
            String brokenLocator =
                    firstNonBlank(
                            persisted.brokenLocator,
                            mapKey
                    );

            HealedLocatorCandidate candidate =
                    new HealedLocatorCandidate(
                            persisted.locatorType,
                            persisted.locatorValue,
                            persisted.confidence,
                            persisted.explanation
                    );

            return HealedLocatorRecord.builder()
                    .brokenLocator(brokenLocator)
                    .candidate(candidate)
                    .screen(persisted.screen)
                    .testName(persisted.testName)
                    .successCount(
                            persisted.successCount
                    )
                    .failureCount(
                            persisted.failureCount
                    )
                    .createdAt(
                            parseInstant(
                                    persisted.createdAt
                            )
                    )
                    .lastUsedAt(
                            parseInstant(
                                    persisted.lastUsedAt
                            )
                    )
                    .build();

        } catch (Exception exception) {

            logger.warn(
                    "[Healing Store] Ignoring invalid persisted record for {}: {}",
                    mapKey,
                    exception.getMessage()
            );

            return null;
        }
    }

    private static Instant parseInstant(
            String value) {

        if (value == null || value.isBlank()) {
            return Instant.now();
        }

        return Instant.parse(
                value.trim()
        );
    }

    private static Path resolveStorePath() {

        String configuredPath =
                System.getProperty(
                        STORE_PATH_PROPERTY
                );

        if (configuredPath == null
                || configuredPath.isBlank()) {

            configuredPath =
                    System.getenv(
                            "AI_HEALING_STORE_PATH"
                    );
        }

        return configuredPath == null
                || configuredPath.isBlank()
                ? DEFAULT_STORE_PATH
                : Path.of(
                        configuredPath.trim()
                );
    }

    private static void moveAtomically(
            Path source,
            Path target)
            throws IOException {

        try {
            Files.move(
                    source,
                    target,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
            );

        } catch (AtomicMoveNotSupportedException exception) {

            Files.move(
                    source,
                    target,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

    private static boolean isDirectoryEmpty(
            Path directory)
            throws IOException {

        try (var contents =
                     Files.list(directory)) {

            return contents.findAny()
                    .isEmpty();
        }
    }

    private static String requireText(
            String value,
            String fieldName) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank"
            );
        }

        return value.trim();
    }

    private static String firstNonBlank(
            String preferred,
            String fallback) {

        if (preferred != null
                && !preferred.isBlank()) {
            return preferred.trim();
        }

        return fallback == null
                ? null
                : fallback.trim();
    }

    /**
     * Jackson persistence DTO.
     *
     * String timestamps avoid requiring jackson-datatype-jsr310.
     */
    public static final class PersistedRecord {

        public String brokenLocator;
        public String locatorType;
        public String locatorValue;
        public int confidence;
        public String explanation;
        public String screen;
        public String testName;
        public int successCount;
        public int failureCount;
        public String createdAt;
        public String lastUsedAt;

        public PersistedRecord() {
        }
    }
}