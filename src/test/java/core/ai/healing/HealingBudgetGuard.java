package core.ai.healing;

import core.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

public final class HealingBudgetGuard {

    private static final Logger logger =
            LoggerUtil.getLogger(HealingBudgetGuard.class);

    private static final Path BUDGET_DIR =
            Path.of(".healing-budget");

    private static final int MAX_PER_RUN =
            readInt("ai.healing.max.calls.per.run", 5);

    private static final int MAX_PER_DAY =
            readInt("ai.healing.max.calls.per.day", 20);

    private static final AtomicInteger RUN_COUNTER =
            new AtomicInteger(0);

    private HealingBudgetGuard() {
    }

    public static boolean allowApiCall(String locatorKey) {

        if (HealedLocatorStore.has(locatorKey)) {
            logger.debug(
                    "[Budget] Skipping API. Locator already healed: {}",
                    locatorKey
            );
            return false;
        }

        int runCount =
                RUN_COUNTER.get();

        if (runCount >= MAX_PER_RUN) {
            logger.warn(
                    "[Budget] Per-run API limit reached {}/{} for {}",
                    runCount,
                    MAX_PER_RUN,
                    locatorKey
            );
            return false;
        }

        int todayCount =
                readDailyCount();

        if (todayCount >= MAX_PER_DAY) {
            logger.warn(
                    "[Budget] Daily API limit reached {}/{} for {}",
                    todayCount,
                    MAX_PER_DAY,
                    locatorKey
            );
            return false;
        }

        RUN_COUNTER.incrementAndGet();
        writeDailyCount(todayCount + 1);

        logger.info(
                "[Budget] API call approved. Run {}/{}, Today {}/{}",
                RUN_COUNTER.get(),
                MAX_PER_RUN,
                todayCount + 1,
                MAX_PER_DAY
        );

        return true;
    }

    public static int getRunCount() {
        return RUN_COUNTER.get();
    }

    public static int getDailyCount() {
        return readDailyCount();
    }

    public static void resetRunCounter() {
        RUN_COUNTER.set(0);
    }

    private static Path dailyFile() {
        return BUDGET_DIR.resolve(
                LocalDate.now() + ".count"
        );
    }

    private static int readDailyCount() {
        try {
            Path file =
                    dailyFile();

            if (!Files.exists(file)) {
                return 0;
            }

            return Integer.parseInt(
                    Files.readString(file).trim()
            );

        } catch (Exception e) {
            return 0;
        }
    }

    private static void writeDailyCount(int count) {
        try {
            Files.createDirectories(BUDGET_DIR);

            Files.writeString(
                    dailyFile(),
                    String.valueOf(count),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

        } catch (IOException e) {
            logger.warn(
                    "[Budget] Could not persist daily counter: {}",
                    e.getMessage()
            );
        }
    }

    private static int readInt(
            String key,
            int defaultValue
    ) {
        try {
            String value =
                    System.getProperty(key);

            if (value == null) {
                value =
                        System.getenv(
                                key.replace('.', '_').toUpperCase()
                        );
            }

            return value != null
                    ? Integer.parseInt(value.trim())
                    : defaultValue;

        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
