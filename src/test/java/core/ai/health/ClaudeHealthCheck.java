package core.ai.health;

import core.ai.client.ClaudeClient;
import core.ai.config.ClaudeConfiguration;
import core.ai.providers.AIRequest;
import core.ai.providers.AIResponse;

import java.util.Objects;

/**
 * Performs a minimal readiness check against the configured Claude client.
 */
public final class ClaudeHealthCheck {

    private static final String HEALTH_SYSTEM_MESSAGE =
            "You are a health-check endpoint. "
                    + "Return only the word OK.";

    private static final String HEALTH_PROMPT =
            "Respond with OK.";

    private final ClaudeConfiguration configuration;
    private final ClaudeClient client;

    public ClaudeHealthCheck(
            ClaudeConfiguration configuration,
            ClaudeClient client) {

        this.configuration = Objects.requireNonNull(
                configuration,
                "Claude configuration must not be null"
        );

        this.client = Objects.requireNonNull(
                client,
                "Claude client must not be null"
        );
    }

    public ClaudeHealthResult check() {

        if (!configuration.isEnabled()) {
            return ClaudeHealthResult.builder()
                    .status(ClaudeHealthStatus.DISABLED)
                    .message(
                            "Claude integration is disabled. "
                                    + "MAPAF will use deterministic fallbacks."
                    )
                    .model(configuration.getModel())
                    .latencyMillis(0)
                    .build();
        }

        if (!configuration.hasApiKey()) {
            return ClaudeHealthResult.builder()
                    .status(ClaudeHealthStatus.FAIL)
                    .message(
                            "Claude API key is not configured"
                    )
                    .model(configuration.getModel())
                    .latencyMillis(0)
                    .build();
        }

        long startedAt = System.nanoTime();

        AIResponse response =
                client.complete(
                        new AIRequest(
                                HEALTH_SYSTEM_MESSAGE,
                                HEALTH_PROMPT
                        )
                );

        long latencyMillis =
                (System.nanoTime() - startedAt)
                        / 1_000_000;

        if (!response.isSuccessful()) {
            return ClaudeHealthResult.builder()
                    .status(ClaudeHealthStatus.FAIL)
                    .message(
                            safeMessage(
                                    response.getErrorMessage()
                            )
                    )
                    .model(configuration.getModel())
                    .latencyMillis(latencyMillis)
                    .build();
        }

        String content = response.getContent();

        if (content == null || content.isBlank()) {
            return ClaudeHealthResult.builder()
                    .status(ClaudeHealthStatus.WARN)
                    .message(
                            "Claude responded successfully "
                                    + "but returned no content"
                    )
                    .model(configuration.getModel())
                    .latencyMillis(latencyMillis)
                    .build();
        }

        return ClaudeHealthResult.builder()
                .status(ClaudeHealthStatus.PASS)
                .message("Claude is available")
                .model(configuration.getModel())
                .latencyMillis(latencyMillis)
                .build();
    }

    private String safeMessage(String value) {
        if (value == null || value.isBlank()) {
            return "Claude health check failed";
        }

        return value;
    }
}
