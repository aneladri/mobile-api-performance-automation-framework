package core.ai.providers;

import core.ai.client.ClaudeClient;
import core.ai.client.ClaudeHttpTransport;
import core.ai.client.HttpClaudeTransport;
import core.ai.config.ClaudeConfiguration;
import core.ai.config.ClaudeConfigurationException;
import core.ai.config.ClaudeConfigurationLoader;

/**
 * Claude implementation of the MAPAF AI provider contract.
 */
public class ClaudeAIProvider implements AIProvider {

    private final ClaudeClient client;

    public ClaudeAIProvider() {
        this(createDefaultClient());
    }

    ClaudeAIProvider(ClaudeClient client) {
        this.client = client;
    }

    @Override
    public AIResponse complete(AIRequest request) {
        return client.complete(request);
    }

    private static ClaudeClient createDefaultClient() {

        try {
            ClaudeConfiguration configuration =
                    ClaudeConfigurationLoader.load();

            ClaudeHttpTransport transport =
                    new HttpClaudeTransport();

            return new ClaudeClient(
                    configuration,
                    transport
            );

        } catch (ClaudeConfigurationException exception) {
            return new ClaudeClient(
                    ClaudeConfiguration.builder()
                            .enabled(false)
                            .build(),
                    new HttpClaudeTransport()
            );
        }
    }
}
