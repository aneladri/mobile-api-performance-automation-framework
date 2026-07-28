package core.ai.locator;

public final class LocatorCandidate {

    private final LocatorStrategy strategy;
    private final String value;
    private final int confidence;
    private final String reasoning;
    private final boolean fallback;

    private LocatorCandidate(Builder builder) {

        this.strategy = builder.strategy == null
                ? LocatorStrategy.UNKNOWN
                : builder.strategy;

        this.value = requireText(
                builder.value,
                "Locator value"
        );

        this.confidence = validateConfidence(
                builder.confidence
        );

        this.reasoning = normalise(
                builder.reasoning
        );

        this.fallback = builder.fallback;
    }

    public static Builder builder() {
        return new Builder();
    }

    public LocatorStrategy getStrategy() {
        return strategy;
    }

    public String getValue() {
        return value;
    }

    public int getConfidence() {
        return confidence;
    }

    public String getReasoning() {
        return reasoning;
    }

    public boolean isFallback() {
        return fallback;
    }

    private static int validateConfidence(int value) {

        if (value < 0 || value > 100) {
            throw new IllegalArgumentException(
                    "Locator confidence must be between 0 and 100"
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

    private static String normalise(String value) {
        return value == null ? null : value.trim();
    }

    public static final class Builder {

        private LocatorStrategy strategy;
        private String value;
        private int confidence;
        private String reasoning;
        private boolean fallback;

        private Builder() {
        }

        public Builder strategy(LocatorStrategy value) {
            this.strategy = value;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder confidence(int value) {
            this.confidence = value;
            return this;
        }

        public Builder reasoning(String value) {
            this.reasoning = value;
            return this;
        }

        public Builder fallback(boolean value) {
            this.fallback = value;
            return this;
        }

        public LocatorCandidate build() {
            return new LocatorCandidate(this);
        }
    }
}
