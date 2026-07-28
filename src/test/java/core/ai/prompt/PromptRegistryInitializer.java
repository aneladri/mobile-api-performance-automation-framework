package core.ai.prompt;

import core.ai.prompt.templates.FailureAnalysisPrompt;

/**
 * Creates a registry containing all production prompt definitions.
 */
public final class PromptRegistryInitializer {

    private PromptRegistryInitializer() {
    }

    public static PromptRegistry createDefaultRegistry() {

        PromptRegistry registry =
                new PromptRegistry();

        registry.register(
                new FailureAnalysisPrompt()
        );

        return registry;
    }
}
