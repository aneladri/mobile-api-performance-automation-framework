package ai.tests;

import core.ai.healing.StrategyLearningRecord;
import core.ai.locator.LocatorStrategy;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Instant;

public class StrategyLearningRecordTest {

    @Test
    public void shouldCreateStrategyLearningRecord() {

        StrategyLearningRecord record =
                StrategyLearningRecord.builder()
                        .platform("Android")
                        .strategy(
                                LocatorStrategy.ACCESSIBILITY_ID
                        )
                        .attempts(10)
                        .successes(8)
                        .failures(2)
                        .totalHealingDurationMillis(1_000)
                        .build();

        Assert.assertEquals(
                record.getPlatform(),
                "android"
        );

        Assert.assertEquals(
                record.getAttempts(),
                10
        );

        Assert.assertEquals(
                record.getSuccessRate(),
                0.8,
                0.001
        );

        Assert.assertEquals(
                record.getFailureRate(),
                0.2,
                0.001
        );

        Assert.assertEquals(
                record.getAverageHealingDurationMillis(),
                100
        );
    }

    @Test
    public void shouldRecordSuccessfulAttempt() {

        Instant timestamp =
                Instant.parse(
                        "2026-07-29T12:00:00Z"
                );

        StrategyLearningRecord updated =
                emptyRecord()
                        .recordSuccess(
                                250,
                                timestamp
                        );

        Assert.assertEquals(
                updated.getAttempts(),
                1
        );

        Assert.assertEquals(
                updated.getSuccesses(),
                1
        );

        Assert.assertEquals(
                updated.getFailures(),
                0
        );

        Assert.assertEquals(
                updated.getTotalHealingDurationMillis(),
                250
        );

        Assert.assertEquals(
                updated.getLastUpdatedAt(),
                timestamp
        );
    }

    @Test
    public void shouldRecordFailedAttempt() {

        StrategyLearningRecord updated =
                emptyRecord()
                        .recordFailure(
                                400,
                                Instant.now()
                        );

        Assert.assertEquals(
                updated.getAttempts(),
                1
        );

        Assert.assertEquals(
                updated.getSuccesses(),
                0
        );

        Assert.assertEquals(
                updated.getFailures(),
                1
        );
    }

    @Test
    public void shouldAccumulateStrategyHistory() {

        StrategyLearningRecord record =
                emptyRecord()
                        .recordSuccess(
                                100,
                                Instant.now()
                        )
                        .recordFailure(
                                300,
                                Instant.now()
                        )
                        .recordSuccess(
                                200,
                                Instant.now()
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
                record.getSuccessRate(),
                2.0 / 3.0,
                0.001
        );

        Assert.assertEquals(
                record.getAverageHealingDurationMillis(),
                200
        );
    }

    @Test
    public void shouldReturnZeroRatesWithoutAttempts() {

        StrategyLearningRecord record =
                emptyRecord();

        Assert.assertEquals(
                record.getSuccessRate(),
                0.0
        );

        Assert.assertEquals(
                record.getFailureRate(),
                0.0
        );

        Assert.assertEquals(
                record.getAverageHealingDurationMillis(),
                0
        );
    }

    @Test(
            expectedExceptions =
                    IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Successes and failures must not exceed attempts"
    )
    public void shouldRejectInvalidAttemptTotals() {

        StrategyLearningRecord.builder()
                .platform("android")
                .strategy(LocatorStrategy.ID)
                .attempts(2)
                .successes(2)
                .failures(1)
                .build();
    }

    @Test(
            expectedExceptions =
                    IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Healing duration must not be negative"
    )
    public void shouldRejectNegativeAttemptDuration() {

        emptyRecord()
                .recordSuccess(
                        -1,
                        Instant.now()
                );
    }

    @Test(
            expectedExceptions =
                    IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Platform must not be blank"
    )
    public void shouldRejectBlankPlatform() {

        StrategyLearningRecord.builder()
                .platform(" ")
                .strategy(LocatorStrategy.ID)
                .build();
    }

    private StrategyLearningRecord emptyRecord() {

        return StrategyLearningRecord.builder()
                .platform("android")
                .strategy(
                        LocatorStrategy.ACCESSIBILITY_ID
                )
                .build();
    }
}
