package core.ai.providers;

public class AIResponse {

    private final String content;
    private final boolean successful;
    private final String errorMessage;

    private AIResponse(
            String content,
            boolean successful,
            String errorMessage
    ) {
        this.content = content;
        this.successful = successful;
        this.errorMessage = errorMessage;
    }

    public static AIResponse success(String content) {
        return new AIResponse(
                content,
                true,
                null
        );
    }

    public static AIResponse failure(String errorMessage) {
        return new AIResponse(
                null,
                false,
                errorMessage
        );
    }

    public String getContent() {
        return content;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
