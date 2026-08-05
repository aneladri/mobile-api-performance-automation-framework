package ai.tests;

import core.ai.healing.StrategyLearningRecord;
import core.ai.healing.StrategyLearningStore;
import core.ai.locator.LocatorStrategy;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class StrategyLearningStoreTest {

    private Path temporaryDirectory;
    private Path storeFile;

    @BeforeMethod
    public void setUp()
            throws Exception {

        temporaryDirectory =
                Files.createTempDirectory(
                        "strategy-learning-store-test"
                );

        storeFile =
                temporaryDirectory.resolve(
                        "strategy-learning.json"
                );

        System.setProperty(
                "ai.healing.strategy.store.path",
                storeFile.toString()
        );

        StrategyLearningStore.clear();
        StrategyLearningStore.reload();
    }

    @AfterMethod
    public void tearDown()
            throws Exception {

        StrategyLearningStore.clear();

        System.clearProperty(
                "ai.healing.strategy.store.path"
        );

        if (Files.exists(temporaryDirectory)) {

            try (var paths =
                         Files.walk(
                                 temporaryDirectory
                         )) {

                paths.sorted(
                                (left, right) ->
                                        right.compareTo(left)
                        )
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (Exception ignored) {
                                // Test cleanup only.
                            }
                        });
            }
        }
    }

    @Test
    public void shouldRecordSuccessfulStrategyAttempt() {

        StrategyLearningStore.recordSuccess(
                "Android",
                LocatorStrategy.ACCESSIBILITY_ID,
                200
        );

        StrategyLearningRecord record =
                StrategyLearningStore.getRecord(
                        "android",
                        LocatorStrategy.ACCESSIBILITY_ID
                );

        Assert.assertNotNull(record);

        Assert.assertEquals(
                record.getAttempts(),
                1
        );

        Assert.assertEquals(
                record.getSuccesses(),
                1
        );

        Assert.assertEquals(
                record.getFailures(),
                0
        );

        Assert.assertEquals(
                record.getTotalHealingDurationMillis(),
                200
        );

        Assert.assertTrue(
                Files.exists(storeFile)
        );
    }

    @Test
    public void shouldRecordFailedStrategyAttempt() {

        StrategyLearningStore.recordFailure(
                "android",
                LocatorStrategy.XPATH,
                350
        );

        StrategyLearningRecord record =
                StrategyLearningStore.getRecord(
                        "android",
                        LocatorStrategy.XPATH
                );

        Assert.assertEquals(
                record.getAttempts(),
                1
        );

        Assert.assertEquals(
                record.getSuccesses(),
                0
        );

        Assert.assertEquals(
                record.getFailures(),
                1
        );
    }

    @Test
    public void shouldAccumulateHistoryForSamePlatformAndStrategy() {

        StrategyLearningStore.recordSuccess(
                "android",
                LocatorStrategy.ID,
                100
        );

        StrategyLearningStore.recordFailure(
                "android",
                LocatorStrategy.ID,
                300
        );

        StrategyLearningStore.recordSuccess(
                "android",
                LocatorStrategy.ID,
                200
        );

        StrategyLearningRecord record =
                StrategyLearningStore.getRecord(
                        "android",
                        LocatorStrategy.ID
                );

        Assert.assertEquals(
                record.getAttempts(),
                3
        );

        Assert.assertEquals(
                record.getSuccesses(),
                2
        );

        Assert.assertEquals(
                record.getFailures(),
                1
        );

        Assert.assertEquals(
                record.getAverageHealingDurationMillis(),
                200
        );
    }

    @Test
    public void shouldKeepPlatformsIndependent() {

        StrategyLearningStore.recordSuccess(
                "android",
                LocatorStrategy.ACCESSIBILITY_ID,
                100
        );

        StrategyLearningStore.recordFailure(
                "ios",
                LocatorStrategy.ACCESSIBILITY_ID,
                200
        );

        Assert.assertEquals(
                StrategyLearningStore.size(),
                2
        );

        Assert.assertEquals(
                StrategyLearningStore.getSuccessRate(
                        "android",
                        LocatorStrategy.ACCESSIBILITY_ID
                ),
                1.0
        );

        Assert.assertEquals(
                StrategyLearningStore.getSuccessRate(
                        "ios",
                        LocatorStrategy.ACCESSIBILITY_ID
                ),
                0.0
        );
    }

    @Test
    public void shouldPersistAndReloadRecords() {

        StrategyLearningStore.recordSuccess(
                "web",
                LocatorStrategy.CSS_SELECTOR,
                125
        );

        StrategyLearningStore.reload();

        StrategyLearningRecord record =
                StrategyLearningStore.getRecord(
                        "web",
                        LocatorStrategy.CSS_SELECTOR
                );

        Assert.assertNotNull(record);

        Assert.assertEquals(
                record.getAttempts(),
                1
        );

        Assert.assertEquals(
                record.getSuccesses(),
                1
        );
    }

    @Test
    public void shouldReturnImmutableRecordCollection() {

        StrategyLearningStore.recordSuccess(
                "android",
                LocatorStrategy.ID,
                100
        );

        Assert.expectThrows(
                UnsupportedOperationException.class,
                () -> StrategyLearningStore
                        .getAllRecords()
                        .clear()
        );
    }

    @Test
    public void shouldReturnZeroSuccessRateForMissingRecord() {

        Assert.assertEquals(
                StrategyLearningStore.getSuccessRate(
                        "android",
                        LocatorStrategy.UNKNOWN
                ),
                0.0
        );
    }

    @Test
    public void shouldRecoverFromMalformedStoreFile()
            throws Exception {

        Files.createDirectories(
                storeFile.getParent()
        );

        Files.writeString(
                storeFile,
                "{not-valid-json"
        );

        StrategyLearningStore.reload();

        Assert.assertEquals(
                StrategyLearningStore.size(),
                0
        );
    }

    @Test(
            expectedExceptions =
                    IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Platform must not be blank"
    )
    public void shouldRejectBlankPlatform() {

        StrategyLearningStore.recordSuccess(
                " ",
                LocatorStrategy.ID,
                100
        );
    }

    @Test(
            expectedExceptions =
                    NullPointerException.class,
            expectedExceptionsMessageRegExp =
                    "Locator strategy must not be null"
    )
    public void shouldRejectMissingStrategy() {

        StrategyLearningStore.recordSuccess(
                "android",
                null,
                100
        );
    }

    @Test(
            expectedExceptions =
                    IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Healing duration must not be negative"
    )
    public void shouldRejectNegativeDuration() {

        StrategyLearningStore.recordFailure(
                "android",
                LocatorStrategy.XPATH,
                -1
        );
    }
}
