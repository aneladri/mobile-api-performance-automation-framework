package core.ai.services;

import core.ai.PromptLoader;
import core.ai.providers.AIProvider;
import core.ai.providers.AIProviderFactory;
import core.ai.providers.AIRequest;
import core.ai.providers.AIResponse;

public class DocumentationAIService {

    private static final String PROMPT_NAME =
            "documentation-analysis.md";

    private final AIProvider provider =
            AIProviderFactory.getProvider();

    public AIResponse analyze(String documentationContext) {

        String systemPrompt =
                PromptLoader.load(PROMPT_NAME);

        AIRequest request =
                new AIRequest(
                        systemPrompt,
                        documentationContext
                );

        return provider.complete(request);
    }
}
