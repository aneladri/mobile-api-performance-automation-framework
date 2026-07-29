package core.ai.healing;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * Determines whether a persisted healed locator record should expire.
 */
public final class HealingExpiryPolicy {

    private final int maximumFailures;
    private final Duration maximumAge;
    private final Duration maximumIdleTime;
    private final double minimumSuccessRatio;
    private final int minimumAttemptsForRatio;

    public HealingExpiryPolicy(
            int maximumFailures,
            Duration maximumAge,
            Duration maximumIdleTime,
            double minimumSuccessRatio,
            int minimumAttemptsForRatio) {

        if (maximumFailures < 1) {
            throw new IllegalArgumentException(
                    "Maximum failures must be at least 1"
            );
        }

        this.maximumAge =
                requirePositiveDuration(
                        maximumAge,
                        "Maximum age"
                );

        this.maximumIdleTime =
                requirePositiveDuration(
                        maximumIdleTime,
                        "Maximum idle time"
                );

        if (minimumSuccessRatio < 0.0
                || minimumSuccessRatio > 1.0) {

            throw new IllegalArgumentException(
                    "Minimum success ratio must be between 0 and 1"
            );
        }

        if (minimumAttemptsForRatio < 1) {
            throw new IllegalArgumentException(
                    "Minimum attempts for ratio must be at least 1"
            );
        }

        this.maximumFailures = maximumFailures;
        this.minimumSuccessRatio = minimumSuccessRatio;
        this.minimumAttemptsForRatio = minimumAttemptsForRatio;
    }

    public static HealingExpiryPolicy defaultPolicy() {

        return new HealingExpiryPolicy(
                3,
                Duration.ofDays(90),
                Duration.ofDays(30),
                0.50,
                4
        );
    }

    public boolean isExpired(
            HealedLocatorRecord record,
            Instant referenceTime) {

        Objects.requireNonNull(
                record,
                "Healed locator record must not be null"
        );

        Instant now =
                referenceTime == null
                        ? Instant.now()
                        : referenceTime;

        return exceedsFailureLimit(record)
                || exceedsMaximumAge(record, now)
                || exceedsMaximumIdleTime(record, now)
                || fallsBelowSuccessRatio(record);
    }

    public boolean exceedsFailureLimit(
            HealedLocatorRecord record) {

        return record.getFailureCount()
                >= maximumFailures;
    }

    public boolean exceedsMaximumAge(
            HealedLocatorRecord record,
            Instant referenceTime) {

        return Duration.between(
                        record.getCreatedAt(),
                        referenceTime
                )
                .compareTo(maximumAge) > 0;
    }

    public boolean exceedsMaximumIdleTime(
            HealedLocatorRecord record,
            Instant referenceTime) {

        return Duration.between(
                        record.getLastUsedAt(),
                        referenceTime
                )
                .compareTo(maximumIdleTime) > 0;
    }

    public boolean fallsBelowSuccessRatio(
            HealedLocatorRecord record) {

        int attempts =
                record.getSuccessCount()
                        + record.getFailureCount();

        if (attempts < minimumAttemptsForRatio) {
            return false;
        }

        double successRatio =
                (double) record.getSuccessCount()
                        / attempts;

        return successRatio
                < minimumSuccessRatio;
    }

    public int getMaximumFailures() {
        return maximumFailures;
    }

    public Duration getMaximumAge() {
        return maximumAge;
    }

    public Duration getMaximumIdleTime() {
        return maximumIdleTime;
    }

    public double getMinimumSuccessRatio() {
        return minimumSuccessRatio;
    }

    public int getMinimumAttemptsForRatio() {
        return minimumAttemptsForRatio;
    }

    private static Duration requirePositiveDuration(
            Duration value,
            String fieldName) {

        if (value == null
                || value.isZero()
                || value.isNegative()) {

            throw new IllegalArgumentException(
                    fieldName + " must be positive"
            );
        }

        return value;
    }
}
