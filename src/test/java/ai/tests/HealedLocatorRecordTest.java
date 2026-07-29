package ai.tests;

import core.ai.healing.HealedLocatorCandidate;
import core.ai.healing.HealedLocatorRecord;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Instant;

public class HealedLocatorRecordTest {

    @Test
    public void shouldCreatePersistentHealingRecord() {

        Instant created =
                Instant.parse(
                        "2026-07-29T10:00:00Z"
                );

        HealedLocatorRecord record =
                HealedLocatorRecord.builder()
                        .brokenLocator(
                                "By.id: old-submit"
                        )
                        .candidate(candidate())
                        .screen("CheckoutScreen")
                        .testName("submitOrderTest")
                        .successCount(2)
                        .failureCount(1)
                        .createdAt(created)
                        .lastUsedAt(created)
                        .build();

        Assert.assertEquals(
                record.getBrokenLocator(),
                "By.id: old-submit"
        );

        Assert.assertEquals(
                record.getSuccessCount(),
                2
        );

        Assert.assertEquals(
                record.getFailureCount(),
                1
        );

        Assert.assertEquals(
                record.getCreatedAt(),
                created
        );
    }

    @Test
    public void shouldRecordSuccessfulReuse() {

        Instant initial =
                Instant.parse(
                        "2026-07-29T10:00:00Z"
                );

        Instant reused =
                Instant.parse(
                        "2026-07-29T11:00:00Z"
                );

        HealedLocatorRecord updated =
                record(initial)
                        .recordSuccess(reused);

        Assert.assertEquals(
                updated.getSuccessCount(),
                1
        );

        Assert.assertEquals(
                updated.getFailureCount(),
                0
        );

        Assert.assertEquals(
                updated.getLastUsedAt(),
                reused
        );

        Assert.assertEquals(
                updated.getCreatedAt(),
                initial
        );
    }

    @Test
    public void shouldRecordFailedReuse() {

        Instant initial =
                Instant.parse(
                        "2026-07-29T10:00:00Z"
                );

        HealedLocatorRecord updated =
                record(initial)
                        .recordFailure(
                                Instant.parse(
                                        "2026-07-29T12:00:00Z"
                                )
                        );

        Assert.assertEquals(
                updated.getFailureCount(),
                1
        );

        Assert.assertEquals(
                updated.getSuccessCount(),
                0
        );
    }

    @Test
    public void shouldMarkRecordStaleAtFailureThreshold() {

        HealedLocatorRecord record =
                HealedLocatorRecord.builder()
                        .brokenLocator(
                                "By.id: old-submit"
                        )
                        .candidate(candidate())
                        .failureCount(3)
                        .build();

        Assert.assertTrue(
                record.isStale(3)
        );

        Assert.assertFalse(
                record.isStale(4)
        );
    }

    @Test(
            expectedExceptions =
                    IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Broken locator must not be blank"
    )
    public void shouldRejectBlankBrokenLocator() {

        HealedLocatorRecord.builder()
                .brokenLocator(" ")
                .candidate(candidate())
                .build();
    }

    @Test(
            expectedExceptions =
                    NullPointerException.class,
            expectedExceptionsMessageRegExp =
                    "Healed locator candidate must not be null"
    )
    public void shouldRejectMissingCandidate() {

        HealedLocatorRecord.builder()
                .brokenLocator(
                        "By.id: old-submit"
                )
                .build();
    }

    @Test(
            expectedExceptions =
                    IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Failure count must not be negative"
    )
    public void shouldRejectNegativeFailureCount() {

        HealedLocatorRecord.builder()
                .brokenLocator(
                        "By.id: old-submit"
                )
                .candidate(candidate())
                .failureCount(-1)
                .build();
    }

    private HealedLocatorRecord record(
            Instant timestamp) {

        return HealedLocatorRecord.builder()
                .brokenLocator(
                        "By.id: old-submit"
                )
                .candidate(candidate())
                .screen("CheckoutScreen")
                .testName("submitOrderTest")
                .createdAt(timestamp)
                .lastUsedAt(timestamp)
                .build();
    }

    private HealedLocatorCandidate candidate() {

        return new HealedLocatorCandidate(
                "ACCESSIBILITY_ID",
                "submit-order",
                91,
                "Stable accessibility identifier"
        );
    }
}
