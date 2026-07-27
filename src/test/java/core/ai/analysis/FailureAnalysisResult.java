package core.ai.analysis;

/**
 * Structured result returned from failure analysis.
 */
public final class FailureAnalysisResult {

    private final boolean successful;
    private final String rootCause;
    private final FailureConfidence confidence;
    private final String recommendedFix;
    private final boolean locatorHealingRecommended;
    private final String additionalEvidence;
    private final String rawResponse;
    private final String errorMessage;

    private FailureAnalysisResult(Builder builder) {

        this.successful = builder.successful;

        this.rootCause =
                normalise(builder.rootCause);

        this.confidence =
                builder.confidence == null
                        ? FailureConfidence.UNKNOWN
                        : builder.confidence;

        this.recommendedFix =
                normalise(builder.recommendedFix);

        this.locatorHealingRecommended =
                builder.locatorHealingRecommended;

        this.additionalEvidence =
                normalise(builder.additionalEvidence);

        this.rawResponse =
                normalise(builder.rawResponse);

        this.errorMessage =
                normalise(builder.errorMessage);

        validate();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static FailureAnalysisResult failure(
            String errorMessage) {

        return builder()
                .successful(false)
                .errorMessage(errorMessage)
                .build();
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getRootCause() {
        return rootCause;
    }

    public FailureConfidence getConfidence() {
        return confidence;
    }

    public String getRecommendedFix() {
        return recommendedFix;
    }

    public boolean isLocatorHealingRecommended() {
        return locatorHealingRecommended;
    }

    public String getAdditionalEvidence() {
        return additionalEvidence;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    private void validate() {

        if (successful) {

            requireText(
                    rootCause,
                    "Root cause"
            );

            requireText(
                    recommendedFix,
                    "Recommended fix"
            );

            requireText(
                    rawResponse,
                    "Raw response"
            );

        } else {

            requireText(
                    errorMessage,
                    "Error message"
            );
        }
    }

    private static String normalise(
            String value) {

        return value == null
                ? null
                : value.trim();
    }

    private static void requireText(
            String value,
            String fieldName) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank"
            );
        }
    }

    public static final class Builder {

        private boolean successful;
        private String rootCause;
        private FailureConfidence confidence;
        private String recommendedFix;
        private boolean locatorHealingRecommended;
        private String additionalEvidence;
        private String rawResponse;
        private String errorMessage;

        private Builder() {
        }

        public Builder successful(boolean value) {
            this.successful = value;
            return this;
        }

        public Builder rootCause(String value) {
            this.rootCause = value;
            return this;
        }

        public Builder confidence(
                FailureConfidence value) {

            this.confidence = value;
            return this;
        }

        public Builder recommendedFix(String value) {
            this.recommendedFix = value;
            return this;
        }

        public Builder locatorHealingRecommended(
                boolean value) {

            this.locatorHealingRecommended = value;
            return this;
        }

        public Builder additionalEvidence(String value) {
            this.additionalEvidence = value;
            return this;
        }

        public Builder rawResponse(String value) {
            this.rawResponse = value;
            return this;
        }

        public Builder errorMessage(String value) {
            this.errorMessage = value;
            return this;
        }

        public FailureAnalysisResult build() {
            return new FailureAnalysisResult(this);
        }
    }
}
