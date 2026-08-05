package core.ai.locator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class LocatorAnalysisResult {

    private final boolean successful;
    private final List<LocatorCandidate> candidates;
    private final String summary;
    private final String rawResponse;
    private final String errorMessage;

    private LocatorAnalysisResult(Builder builder) {

        this.successful = builder.successful;

        this.candidates = Collections.unmodifiableList(
                new ArrayList<>(builder.candidates)
        );

        this.summary = normalise(builder.summary);
        this.rawResponse = normalise(builder.rawResponse);
        this.errorMessage = normalise(builder.errorMessage);

        validate();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static LocatorAnalysisResult failure(
            String errorMessage) {

        return builder()
                .successful(false)
                .errorMessage(errorMessage)
                .build();
    }

    public boolean isSuccessful() {
        return successful;
    }

    public List<LocatorCandidate> getCandidates() {
        return candidates;
    }

    public LocatorCandidate getBestCandidate() {
        return candidates.isEmpty()
                ? null
                : candidates.get(0);
    }

    public String getSummary() {
        return summary;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    private void validate() {

        if (successful) {
            if (candidates.isEmpty()) {
                throw new IllegalArgumentException(
                        "Successful locator analysis requires at least one candidate"
                );
            }

            requireText(rawResponse, "Raw response");
        } else {
            requireText(errorMessage, "Error message");
        }
    }

    private static String normalise(String value) {
        return value == null ? null : value.trim();
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
        private final List<LocatorCandidate> candidates =
                new ArrayList<>();
        private String summary;
        private String rawResponse;
        private String errorMessage;

        private Builder() {
        }

        public Builder successful(boolean value) {
            this.successful = value;
            return this;
        }

        public Builder addCandidate(
                LocatorCandidate candidate) {

            if (candidate == null) {
                throw new NullPointerException(
                        "Locator candidate must not be null"
                );
            }

            this.candidates.add(candidate);
            return this;
        }

        public Builder candidates(
                List<LocatorCandidate> values) {

            if (values == null) {
                throw new NullPointerException(
                        "Locator candidates must not be null"
                );
            }

            values.forEach(this::addCandidate);
            return this;
        }

        public Builder summary(String value) {
            this.summary = value;
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

        public LocatorAnalysisResult build() {
            return new LocatorAnalysisResult(this);
        }
    }
}
