package core.ai.locator;

public final class LocatorAnalysisRequest {

    private final String platform;
    private final String screenName;
    private final String elementDescription;
    private final String existingLocator;
    private final String failureMessage;
    private final String pageSource;

    private LocatorAnalysisRequest(Builder builder) {

        this.platform =
                requireText(builder.platform, "Platform");

        this.screenName =
                requireText(builder.screenName, "Screen name");

        this.elementDescription =
                requireText(
                        builder.elementDescription,
                        "Element description"
                );

        this.existingLocator =
                normalise(builder.existingLocator);

        this.failureMessage =
                normalise(builder.failureMessage);

        this.pageSource =
                requireText(
                        builder.pageSource,
                        "Page source"
                );
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getPlatform() {
        return platform;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getElementDescription() {
        return elementDescription;
    }

    public String getExistingLocator() {
        return existingLocator;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public String getPageSource() {
        return pageSource;
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

        private String platform;
        private String screenName;
        private String elementDescription;
        private String existingLocator;
        private String failureMessage;
        private String pageSource;

        private Builder() {
        }

        public Builder platform(String value) {
            this.platform = value;
            return this;
        }

        public Builder screenName(String value) {
            this.screenName = value;
            return this;
        }

        public Builder elementDescription(String value) {
            this.elementDescription = value;
            return this;
        }

        public Builder existingLocator(String value) {
            this.existingLocator = value;
            return this;
        }

        public Builder failureMessage(String value) {
            this.failureMessage = value;
            return this;
        }

        public Builder pageSource(String value) {
            this.pageSource = value;
            return this;
        }

        public LocatorAnalysisRequest build() {
            return new LocatorAnalysisRequest(this);
        }
    }
}
