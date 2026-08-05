package core.ai.services;

import core.ai.PromptLoader;
import core.ai.providers.AIProvider;
import core.ai.providers.AIProviderFactory;
import core.ai.providers.AIRequest;
import core.ai.providers.AIResponse;

public class PerformanceAIService {

    private static final String PROMPT_NAME =
            "performance-analysis.md";

    private final AIProvider provider =
            AIProviderFactory.getProvider();

    public AIResponse analyze(String performanceContext) {

        String systemPrompt =
                PromptLoader.load(PROMPT_NAME);

        AIRequest request =
                new AIRequest(
                        systemPrompt,
                        performanceContext
                );

        return provider.complete(request);
    }
}
