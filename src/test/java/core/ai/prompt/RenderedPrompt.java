package core.ai.prompt;

import java.util.Objects;

public final class RenderedPrompt {

    private final String promptName;
    private final String promptVersion;
    private final String systemMessage;
    private final String userMessage;

    private RenderedPrompt(Builder builder) {

        this.promptName =
                require(builder.promptName, "Prompt name");

        this.promptVersion =
                require(builder.promptVersion, "Prompt version");

        this.systemMessage =
                require(builder.systemMessage, "System message");

        this.userMessage =
                require(builder.userMessage, "User message");
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getPromptName() {
        return promptName;
    }

    public String getPromptVersion() {
        return promptVersion;
    }

    public String getSystemMessage() {
        return systemMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }

    private static String require(
            String value,
            String field) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    field + " must not be blank"
            );
        }

        return value;
    }

    public static final class Builder {

        private String promptName;
        private String promptVersion;
        private String systemMessage;
        private String userMessage;

        public Builder promptName(String value) {
            this.promptName = value;
            return this;
        }

        public Builder promptVersion(String value) {
            this.promptVersion = value;
            return this;
        }

        public Builder systemMessage(String value) {
            this.systemMessage = value;
            return this;
        }

        public Builder userMessage(String value) {
            this.userMessage = value;
            return this;
        }

        public RenderedPrompt build() {
            return new RenderedPrompt(this);
        }
    }
}
