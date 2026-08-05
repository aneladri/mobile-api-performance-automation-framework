package ai.tests;

import core.ai.healing.HealedLocatorCandidate;
import core.ai.healing.HealedLocatorRecord;
import core.ai.healing.HealingExpiryPolicy;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.Instant;

public class HealingExpiryPolicyTest {

    private static final Instant NOW =
            Instant.parse(
                    "2026-07-29T12:00:00Z"
            );

    @Test
    public void shouldExpireAtFailureThreshold() {

        HealingExpiryPolicy policy =
                policy();

        Assert.assertTrue(
                policy.isExpired(
                        record(
                                2,
                                3,
                                NOW.minus(Duration.ofDays(2)),
                                NOW.minus(Duration.ofHours(1))
                        ),
                        NOW
                )
        );
    }

    @Test
    public void shouldExpireWhenMaximumAgeIsExceeded() {

        Assert.assertTrue(
                policy().isExpired(
                        record(
                                5,
                                0,
                                NOW.minus(Duration.ofDays(91)),
                                NOW.minus(Duration.ofHours(1))
                        ),
                        NOW
                )
        );
    }

    @Test
    public void shouldExpireWhenMaximumIdleTimeIsExceeded() {

        Assert.assertTrue(
                policy().isExpired(
                        record(
                                5,
                                0,
                                NOW.minus(Duration.ofDays(10)),
                                NOW.minus(Duration.ofDays(31))
                        ),
                        NOW
                )
        );
    }

    @Test
    public void shouldExpireWhenSuccessRatioIsTooLow() {

        Assert.assertTrue(
                policy().isExpired(
                        record(
                                1,
                                3,
                                NOW.minus(Duration.ofDays(2)),
                                NOW.minus(Duration.ofHours(1))
                        ),
                        NOW
                )
        );
    }

    @Test
    public void shouldNotApplyRatioBeforeMinimumAttempts() {

        Assert.assertFalse(
                policy().isExpired(
                        record(
                                0,
                                2,
                                NOW.minus(Duration.ofDays(2)),
                                NOW.minus(Duration.ofHours(1))
                        ),
                        NOW
                )
        );
    }

    @Test
    public void shouldKeepHealthyRecord() {

        Assert.assertFalse(
                policy().isExpired(
                        record(
                                8,
                                1,
                                NOW.minus(Duration.ofDays(15)),
                                NOW.minus(Duration.ofDays(2))
                        ),
                        NOW
                )
        );
    }

    @Test(
            expectedExceptions =
                    IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Maximum failures must be at least 1"
    )
    public void shouldRejectInvalidFailureLimit() {

        new HealingExpiryPolicy(
                0,
                Duration.ofDays(90),
                Duration.ofDays(30),
                0.5,
                4
        );
    }

    @Test(
            expectedExceptions =
                    IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Minimum success ratio must be between 0 and 1"
    )
    public void shouldRejectInvalidSuccessRatio() {

        new HealingExpiryPolicy(
                3,
                Duration.ofDays(90),
                Duration.ofDays(30),
                1.1,
                4
        );
    }

    private HealingExpiryPolicy policy() {

        return new HealingExpiryPolicy(
                3,
                Duration.ofDays(90),
                Duration.ofDays(30),
                0.50,
                4
        );
    }

    private HealedLocatorRecord record(
            int successes,
            int failures,
            Instant createdAt,
            Instant lastUsedAt) {

        return HealedLocatorRecord.builder()
                .brokenLocator(
                        "By.id: old-submit"
                )
                .candidate(
                        new HealedLocatorCandidate(
                                "ACCESSIBILITY_ID",
                                "submit-order",
                                91,
                                "Stable locator"
                        )
                )
                .successCount(successes)
                .failureCount(failures)
                .createdAt(createdAt)
                .lastUsedAt(lastUsedAt)
                .build();
    }
}