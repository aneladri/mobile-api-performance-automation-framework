package core.ai.client;

/**
 * Represents a controlled Claude client or transport failure.
 */
public class ClaudeClientException extends RuntimeException {

    private final Integer statusCode;
    private final boolean retryable;

    public ClaudeClientException(
            String message,
            boolean retryable) {

        this(message, null, retryable, null);
    }

    public ClaudeClientException(
            String message,
            Integer statusCode,
            boolean retryable) {

        this(message, statusCode, retryable, null);
    }

    public ClaudeClientException(
            String message,
            boolean retryable,
            Throwable cause) {

        this(message, null, retryable, cause);
    }

    public ClaudeClientException(
            String message,
            Integer statusCode,
            boolean retryable,
            Throwable cause) {

        super(message, cause);
        this.statusCode = statusCode;
        this.retryable = retryable;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public boolean isRetryable() {
        return retryable;
    }
}
