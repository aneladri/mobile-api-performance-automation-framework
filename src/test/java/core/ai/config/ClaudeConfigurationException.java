package core.ai.config;

/**
 * Represents an invalid or incomplete Claude configuration.
 */
public class ClaudeConfigurationException extends RuntimeException {

    public ClaudeConfigurationException(String message) {
        super(message);
    }

    public ClaudeConfigurationException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}
