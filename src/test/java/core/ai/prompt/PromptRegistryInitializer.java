package core.ai.prompt;

import core.ai.prompt.templates.FailureAnalysisPrompt;
import core.ai.prompt.templates.LocatorAnalysisPrompt;

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

        registry.register(
                new LocatorAnalysisPrompt()
        );

        return registry;
    }
}