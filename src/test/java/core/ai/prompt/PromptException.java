package core.ai.prompt;

/**
 * Exception thrown by the prompt framework.
 */
public class PromptException extends RuntimeException {

    public PromptException(String message) {
        super(message);
    }

    public PromptException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}
