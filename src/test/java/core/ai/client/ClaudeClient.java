package core.ai.client;

import core.ai.config.ClaudeConfiguration;
import core.ai.providers.AIRequest;
import core.ai.providers.AIResponse;

import java.util.Objects;

/**
 * Reusable Claude client responsible for validation, retries and
 * controlled framework responses.
 */
public final class ClaudeClient {

    private final ClaudeConfiguration configuration;
    private final ClaudeHttpTransport transport;

    public ClaudeClient(
            ClaudeConfiguration configuration,
            ClaudeHttpTransport transport) {

        this.configuration = Objects.requireNonNull(
                configuration,
                "Claude configuration must not be null"
        );

        this.transport = Objects.requireNonNull(
                transport,
                "Claude transport must not be null"
        );
    }

    public AIResponse complete(AIRequest request) {

        if (!configuration.isEnabled()) {
            return AIResponse.failure(
                    "Claude integration is disabled"
            );
        }

        String validationError = validateRequest(request);

        if (validationError != null) {
            return AIResponse.failure(validationError);
        }

        int maximumAttempts =
                configuration.getMaxRetries() + 1;

        ClaudeClientException lastFailure = null;

        for (int attempt = 1;
             attempt <= maximumAttempts;
             attempt++) {

            try {
                AIResponse response =
                        transport.send(
                                configuration,
                                request
                        );

                if (response == null) {
                    return AIResponse.failure(
                            "Claude transport returned no response"
                    );
                }

                return response;

            } catch (ClaudeClientException exception) {
                lastFailure = exception;

                boolean retriesRemaining =
                        attempt < maximumAttempts;

                if (!exception.isRetryable()
                        || !retriesRemaining) {

                    return AIResponse.failure(
                            safeMessage(exception)
                    );
                }
            }
        }

        return AIResponse.failure(
                lastFailure == null
                        ? "Claude request failed"
                        : safeMessage(lastFailure)
        );
    }

    public ClaudeConfiguration getConfiguration() {
        return configuration;
    }

    private String validateRequest(AIRequest request) {

        if (request == null) {
            return "Claude request must not be null";
        }

        if (request.getPrompt() == null
                || request.getPrompt().isBlank()) {

            return "Claude prompt must not be blank";
        }

        return null;
    }

    private String safeMessage(
            ClaudeClientException exception) {

        String message = exception.getMessage();

        if (message == null || message.isBlank()) {
            return "Claude request failed";
        }

        return message;
    }
}
