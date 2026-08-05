package core.ai.healing;

import com.fasterxml.jackson.core.type.TypeReference;
import core.ai.locator.LocatorStrategy;
import core.ai.persistence.JsonPersistentStore;
import core.ai.persistence.PersistentStoreException;
import core.ai.persistence.StorePaths;
import core.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Persists locator-strategy performance by platform.
 */
public final class StrategyLearningStore
        extends JsonPersistentStore<StrategyLearningRecord> {

    private static final Logger logger =
            LoggerUtil.getLogger(StrategyLearningStore.class);

    private static final String STORE_PATH_PROPERTY =
            "ai.healing.strategy.store.path";

    private static final StrategyLearningStore INSTANCE =
            new StrategyLearningStore();

    private static final Map<String, StrategyLearningRecord> CACHE =
            new ConcurrentHashMap<>();

    private static final Object STORE_LOCK =
            new Object();

    static {
        reload();
    }

    private StrategyLearningStore() {
    }

    public static void recordSuccess(
            String platform,
            LocatorStrategy strategy,
            long durationMillis) {

        update(
                platform,
                strategy,
                durationMillis,
                true
        );
    }

    public static void recordFailure(
            String platform,
            LocatorStrategy strategy,
            long durationMillis) {

        update(
                platform,
                strategy,
                durationMillis,
                false
        );
    }

    public static StrategyLearningRecord getRecord(
            String platform,
            LocatorStrategy strategy) {

        if (platform == null
                || platform.isBlank()
                || strategy == null) {
            return null;
        }

        return CACHE.get(
                key(platform, strategy)
        );
    }

    public static double getSuccessRate(
            String platform,
            LocatorStrategy strategy) {

        StrategyLearningRecord record =
                getRecord(platform, strategy);

        return record == null
                ? 0.0
                : record.getSuccessRate();
    }

    public static Collection<StrategyLearningRecord> getAllRecords() {

        return Collections.unmodifiableCollection(
                CACHE.values()
        );
    }

    public static int size() {
        return CACHE.size();
    }

    public static void reload() {

        synchronized (STORE_LOCK) {

            CACHE.clear();

            Path path =
                    INSTANCE.storePath();

            if (!Files.exists(path)) {
                return;
            }

            try {
                Map<String, PersistedRecord> persisted =
                        OBJECT_MAPPER.readValue(
                                path.toFile(),
                                new TypeReference<
                                        Map<String, PersistedRecord>>() {
                                }
                        );

                if (persisted == null) {
                    return;
                }

                persisted.forEach(
                        (key, value) -> {

                            StrategyLearningRecord record =
                                    toDomainRecord(value);

                            if (record != null) {
                                CACHE.put(
                                        key(
                                                record.getPlatform(),
                                                record.getStrategy()
                                        ),
                                        record
                                );
                            }
                        }
                );

                logger.info(
                        "[Strategy Learning] Loaded {} record(s) from {}",
                        CACHE.size(),
                        path
                );

            } catch (Exception exception) {

                CACHE.clear();

                logger.warn(
                        "[Strategy Learning] Could not load {}: {}",
                        path,
                        exception.getMessage()
                );
            }
        }
    }

    public static void clear() {

        synchronized (STORE_LOCK) {

            CACHE.clear();

            try {
                INSTANCE.deleteStore();
            } catch (PersistentStoreException exception) {

                logger.warn(
                        "[Strategy Learning] Could not clear store: {}",
                        exception.getMessage()
                );
            }
        }
    }

    @Override
    protected Path storePath() {

        String configured =
                System.getProperty(
                        STORE_PATH_PROPERTY
                );

        if (configured == null
                || configured.isBlank()) {

            configured =
                    System.getenv(
                            "AI_HEALING_STRATEGY_STORE_PATH"
                    );
        }

        return configured == null
                || configured.isBlank()
                ? StorePaths.STRATEGY_LEARNING
                : Path.of(
                        configured.trim()
                );
    }

    private static void update(
            String platform,
            LocatorStrategy strategy,
            long durationMillis,
            boolean successful) {

        String normalisedPlatform =
                requirePlatform(platform);

        if (strategy == null) {
            throw new NullPointerException(
                    "Locator strategy must not be null"
            );
        }

        if (durationMillis < 0) {
            throw new IllegalArgumentException(
                    "Healing duration must not be negative"
            );
        }

        synchronized (STORE_LOCK) {

            String key =
                    key(
                            normalisedPlatform,
                            strategy
                    );

            StrategyLearningRecord existing =
                    CACHE.get(key);

            if (existing == null) {

                existing =
                        StrategyLearningRecord.builder()
                                .platform(normalisedPlatform)
                                .strategy(strategy)
                                .build();
            }

            StrategyLearningRecord updated =
                    successful
                            ? existing.recordSuccess(
                                    durationMillis,
                                    Instant.now()
                            )
                            : existing.recordFailure(
                                    durationMillis,
                                    Instant.now()
                            );

            CACHE.put(
                    key,
                    updated
            );

            persist();
        }
    }

    private static void persist() {

        Map<String, PersistedRecord> persisted =
                new LinkedHashMap<>();

        CACHE.forEach(
                (key, record) ->
                        persisted.put(
                                key,
                                fromDomainRecord(record)
                        )
        );

        INSTANCE.writeJson(
                persisted
        );
    }

    private static PersistedRecord fromDomainRecord(
            StrategyLearningRecord record) {

        PersistedRecord persisted =
                new PersistedRecord();

        persisted.platform =
                record.getPlatform();

        persisted.strategy =
                record.getStrategy().name();

        persisted.attempts =
                record.getAttempts();

        persisted.successes =
                record.getSuccesses();

        persisted.failures =
                record.getFailures();

        persisted.totalHealingDurationMillis =
                record.getTotalHealingDurationMillis();

        persisted.createdAt =
                record.getCreatedAt().toString();

        persisted.lastUpdatedAt =
                record.getLastUpdatedAt().toString();

        return persisted;
    }

    private static StrategyLearningRecord toDomainRecord(
            PersistedRecord persisted) {

        if (persisted == null) {
            return null;
        }

        try {
            return StrategyLearningRecord.builder()
                    .platform(persisted.platform)
                    .strategy(
                            LocatorStrategy.from(
                                    persisted.strategy
                            )
                    )
                    .attempts(persisted.attempts)
                    .successes(persisted.successes)
                    .failures(persisted.failures)
                    .totalHealingDurationMillis(
                            persisted.totalHealingDurationMillis
                    )
                    .createdAt(
                            parseInstant(
                                    persisted.createdAt
                            )
                    )
                    .lastUpdatedAt(
                            parseInstant(
                                    persisted.lastUpdatedAt
                            )
                    )
                    .build();

        } catch (Exception exception) {

            logger.warn(
                    "[Strategy Learning] Ignoring invalid persisted record: {}",
                    exception.getMessage()
            );

            return null;
        }
    }

    private static Instant parseInstant(
            String value) {

        return value == null
                || value.isBlank()
                ? Instant.now()
                : Instant.parse(value);
    }

    private static String key(
            String platform,
            LocatorStrategy strategy) {

        return platform.trim()
                .toLowerCase()
                + "::"
                + strategy.name();
    }

    private static String requirePlatform(
            String platform) {

        if (platform == null
                || platform.isBlank()) {

            throw new IllegalArgumentException(
                    "Platform must not be blank"
            );
        }

        return platform.trim()
                .toLowerCase();
    }

    public static final class PersistedRecord {

        public String platform;
        public String strategy;
        public int attempts;
        public int successes;
        public int failures;
        public long totalHealingDurationMillis;
        public String createdAt;
        public String lastUpdatedAt;

        public PersistedRecord() {
        }
    }
}
