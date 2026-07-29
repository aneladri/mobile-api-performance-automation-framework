package core.ai.healing;

import java.time.Instant;
import java.util.Objects;

/**
 * Persistent learning record for a healed locator.
 */
public final class HealedLocatorRecord {

    private final String brokenLocator;
    private final HealedLocatorCandidate candidate;
    private final String screen;
    private final String testName;
    private final int successCount;
    private final int failureCount;
    private final Instant createdAt;
    private final Instant lastUsedAt;

    private HealedLocatorRecord(Builder builder) {

        this.brokenLocator =
                requireText(
                        builder.brokenLocator,
                        "Broken locator"
                );

        this.candidate =
                Objects.requireNonNull(
                        builder.candidate,
                        "Healed locator candidate must not be null"
                );

        this.screen =
                normalise(builder.screen);

        this.testName =
                normalise(builder.testName);

        this.successCount =
                validateNonNegative(
                        builder.successCount,
                        "Success count"
                );

        this.failureCount =
                validateNonNegative(
                        builder.failureCount,
                        "Failure count"
                );

        this.createdAt =
                builder.createdAt == null
                        ? Instant.now()
                        : builder.createdAt;

        this.lastUsedAt =
                builder.lastUsedAt == null
                        ? this.createdAt
                        : builder.lastUsedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getBrokenLocator() {
        return brokenLocator;
    }

    public HealedLocatorCandidate getCandidate() {
        return candidate;
    }

    public String getScreen() {
        return screen;
    }

    public String getTestName() {
        return testName;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastUsedAt() {
        return lastUsedAt;
    }

    public HealedLocatorRecord recordSuccess(
            Instant timestamp) {

        return builder()
                .brokenLocator(brokenLocator)
                .candidate(candidate)
                .screen(screen)
                .testName(testName)
                .successCount(successCount + 1)
                .failureCount(failureCount)
                .createdAt(createdAt)
                .lastUsedAt(
                        timestamp == null
                                ? Instant.now()
                                : timestamp
                )
                .build();
    }

    public HealedLocatorRecord recordFailure(
            Instant timestamp) {

        return builder()
                .brokenLocator(brokenLocator)
                .candidate(candidate)
                .screen(screen)
                .testName(testName)
                .successCount(successCount)
                .failureCount(failureCount + 1)
                .createdAt(createdAt)
                .lastUsedAt(
                        timestamp == null
                                ? Instant.now()
                                : timestamp
                )
                .build();
    }

    public boolean isStale(
            int maximumFailures) {

        return failureCount >= maximumFailures;
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

    private static String normalise(
            String value) {

        return value == null
                ? null
                : value.trim();
    }

    public static final class Builder {

        private String brokenLocator;
        private HealedLocatorCandidate candidate;
        private String screen;
        private String testName;
        private int successCount;
        private int failureCount;
        private Instant createdAt;
        private Instant lastUsedAt;

        private Builder() {
        }

        public Builder brokenLocator(String value) {
            this.brokenLocator = value;
            return this;
        }

        public Builder candidate(
                HealedLocatorCandidate value) {
            this.candidate = value;
            return this;
        }

        public Builder screen(String value) {
            this.screen = value;
            return this;
        }

        public Builder testName(String value) {
            this.testName = value;
            return this;
        }

        public Builder successCount(int value) {
            this.successCount = value;
            return this;
        }

        public Builder failureCount(int value) {
            this.failureCount = value;
            return this;
        }

        public Builder createdAt(Instant value) {
            this.createdAt = value;
            return this;
        }

        public Builder lastUsedAt(Instant value) {
            this.lastUsedAt = value;
            return this;
        }

        public HealedLocatorRecord build() {
            return new HealedLocatorRecord(this);
        }
    }
}
