package core.ai.analysis;

/**
 * Immutable failure evidence supplied to the AI analysis service.
 */
public final class FailureAnalysisRequest {

    private final String testName;
    private final String testType;
    private final String pageName;
    private final String expectedResult;
    private final String actualResult;
    private final String locator;
    private final String errorMessage;
    private final String stackTrace;

    private FailureAnalysisRequest(Builder builder) {

        this.testName =
                requireText(
                        builder.testName,
                        "Test name"
                );

        this.testType =
                requireText(
                        builder.testType,
                        "Test type"
                );

        this.pageName =
                requireText(
                        builder.pageName,
                        "Page name"
                );

        this.expectedResult =
                requireText(
                        builder.expectedResult,
                        "Expected result"
                );

        this.actualResult =
                requireText(
                        builder.actualResult,
                        "Actual result"
                );

        this.locator =
                requireText(
                        builder.locator,
                        "Locator"
                );

        this.errorMessage =
                requireText(
                        builder.errorMessage,
                        "Error message"
                );

        this.stackTrace =
                requireText(
                        builder.stackTrace,
                        "Stack trace"
                );
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getTestName() {
        return testName;
    }

    public String getTestType() {
        return testType;
    }

    public String getPageName() {
        return pageName;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public String getActualResult() {
        return actualResult;
    }

    public String getLocator() {
        return locator;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getStackTrace() {
        return stackTrace;
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

        private String testName;
        private String testType;
        private String pageName;
        private String expectedResult;
        private String actualResult;
        private String locator;
        private String errorMessage;
        private String stackTrace;

        private Builder() {
        }

        public Builder testName(String value) {
            this.testName = value;
            return this;
        }

        public Builder testType(String value) {
            this.testType = value;
            return this;
        }

        public Builder pageName(String value) {
            this.pageName = value;
            return this;
        }

        public Builder expectedResult(String value) {
            this.expectedResult = value;
            return this;
        }

        public Builder actualResult(String value) {
            this.actualResult = value;
            return this;
        }

        public Builder locator(String value) {
            this.locator = value;
            return this;
        }

        public Builder errorMessage(String value) {
            this.errorMessage = value;
            return this;
        }

        public Builder stackTrace(String value) {
            this.stackTrace = value;
            return this;
        }

        public FailureAnalysisRequest build() {
            return new FailureAnalysisRequest(this);
        }
    }
}
