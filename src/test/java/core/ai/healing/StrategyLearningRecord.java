package core.ai.healing;

import core.ai.locator.LocatorStrategy;

import java.time.Instant;
import java.util.Objects;

/**
 * Tracks the historical performance of a locator strategy.
 */
public final class StrategyLearningRecord {

    private final String platform;
    private final LocatorStrategy strategy;
    private final int attempts;
    private final int successes;
    private final int failures;
    private final long totalHealingDurationMillis;
    private final Instant createdAt;
    private final Instant lastUpdatedAt;

    private StrategyLearningRecord(
            Builder builder) {

        this.platform =
                requireText(
                        builder.platform,
                        "Platform"
                )
                        .toLowerCase();

        this.strategy =
                Objects.requireNonNull(
                        builder.strategy,
                        "Locator strategy must not be null"
                );

        this.attempts =
                validateNonNegative(
                        builder.attempts,
                        "Attempts"
                );

        this.successes =
                validateNonNegative(
                        builder.successes,
                        "Successes"
                );

        this.failures =
                validateNonNegative(
                        builder.failures,
                        "Failures"
                );

        if (successes + failures > attempts) {
            throw new IllegalArgumentException(
                    "Successes and failures must not exceed attempts"
            );
        }

        this.totalHealingDurationMillis =
                validateNonNegative(
                        builder.totalHealingDurationMillis,
                        "Total healing duration"
                );

        this.createdAt =
                builder.createdAt == null
                        ? Instant.now()
                        : builder.createdAt;

        this.lastUpdatedAt =
                builder.lastUpdatedAt == null
                        ? createdAt
                        : builder.lastUpdatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getPlatform() {
        return platform;
    }

    public LocatorStrategy getStrategy() {
        return strategy;
    }

    public int getAttempts() {
        return attempts;
    }

    public int getSuccesses() {
        return successes;
    }

    public int getFailures() {
        return failures;
    }

    public long getTotalHealingDurationMillis() {
        return totalHealingDurationMillis;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public double getSuccessRate() {

        if (attempts == 0) {
            return 0.0;
        }

        return (double) successes / attempts;
    }

    public double getFailureRate() {

        if (attempts == 0) {
            return 0.0;
        }

        return (double) failures / attempts;
    }

    public long getAverageHealingDurationMillis() {

        if (attempts == 0) {
            return 0;
        }

        return totalHealingDurationMillis / attempts;
    }

    public StrategyLearningRecord recordSuccess(
            long durationMillis,
            Instant timestamp) {

        return builder()
                .platform(platform)
                .strategy(strategy)
                .attempts(attempts + 1)
                .successes(successes + 1)
                .failures(failures)
                .totalHealingDurationMillis(
                        totalHealingDurationMillis
                                + validateDuration(durationMillis)
                )
                .createdAt(createdAt)
                .lastUpdatedAt(
                        timestamp == null
                                ? Instant.now()
                                : timestamp
                )
                .build();
    }

    public StrategyLearningRecord recordFailure(
            long durationMillis,
            Instant timestamp) {

        return builder()
                .platform(platform)
                .strategy(strategy)
                .attempts(attempts + 1)
                .successes(successes)
                .failures(failures + 1)
                .totalHealingDurationMillis(
                        totalHealingDurationMillis
                                + validateDuration(durationMillis)
                )
                .createdAt(createdAt)
                .lastUpdatedAt(
                        timestamp == null
                                ? Instant.now()
                                : timestamp
                )
                .build();
    }

    private static long validateDuration(
            long durationMillis) {

        if (durationMillis < 0) {
            throw new IllegalArgumentException(
                    "Healing duration must not be negative"
            );
        }

        return durationMillis;
    }

    private static int validateNonNegative(
            int value,
            String fieldName) {

        if (value < 0) {
            throw new IllegalArgumentException(
                    fieldName + " must not be negative"
            );
        }

        return value;
    }

    private static long validateNonNegative(
            long value,
            String fieldName) {

        if (value < 0) {
            throw new IllegalArgumentException(
                    fieldName + " must not be negative"
            );
        }

        return value;
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

    public static final class Builder {

        private String platform;
        private LocatorStrategy strategy;
        private int attempts;
        private int successes;
        private int failures;
        private long totalHealingDurationMillis;
        private Instant createdAt;
        private Instant lastUpdatedAt;

        private Builder() {
        }

        public Builder platform(String value) {
            this.platform = value;
            return this;
        }

        public Builder strategy(
                LocatorStrategy value) {
            this.strategy = value;
            return this;
        }

        public Builder attempts(int value) {
            this.attempts = value;
            return this;
        }

        public Builder successes(int value) {
            this.successes = value;
            return this;
        }

        public Builder failures(int value) {
            this.failures = value;
            return this;
        }

        public Builder totalHealingDurationMillis(
                long value) {
            this.totalHealingDurationMillis = value;
            return this;
        }

        public Builder createdAt(
                Instant value) {
            this.createdAt = value;
            return this;
        }

        public Builder lastUpdatedAt(
                Instant value) {
            this.lastUpdatedAt = value;
            return this;
        }

        public StrategyLearningRecord build() {
            return new StrategyLearningRecord(this);
        }
    }
}
