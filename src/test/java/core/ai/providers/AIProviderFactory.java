package core.ai.providers;

import core.config.ConfigManager;

public final class AIProviderFactory {

    private AIProviderFactory() {
    }

    public static AIProvider getProvider() {

        String provider =
                ConfigManager.get("aiProvider") != null
                        ? ConfigManager.get("aiProvider")
                        : "claude";

        return switch (provider.toLowerCase()) {
            case "claude" -> new ClaudeAIProvider();

            default -> throw new RuntimeException(
                    "Unsupported AI provider: " + provider
            );
        };
    }
}
