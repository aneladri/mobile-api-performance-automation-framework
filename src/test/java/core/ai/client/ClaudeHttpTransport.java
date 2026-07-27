package core.ai.client;

import core.ai.config.ClaudeConfiguration;
import core.ai.providers.AIRequest;
import core.ai.providers.AIResponse;

/**
 * Abstraction for Claude HTTP communication.
 *
 * Implementations perform one request only. Retry behaviour belongs
 * to ClaudeClient.
 */
public interface ClaudeHttpTransport {

    AIResponse send(
            ClaudeConfiguration configuration,
            AIRequest request);
}
