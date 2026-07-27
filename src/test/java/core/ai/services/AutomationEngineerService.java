package core.ai.services;

import core.ai.PromptLoader;
import core.ai.models.AutomationGenerationRequest;
import core.ai.models.AutomationGenerationResponse;
import core.ai.providers.AIProvider;
import core.ai.providers.AIProviderFactory;
import core.ai.providers.AIRequest;
import core.ai.providers.AIResponse;

public class AutomationEngineerService {

        private static final String PROMPT = "automation-engineer.md";

        private final AIProvider provider = AIProviderFactory.getProvider();

        public AutomationGenerationResponse generate(
                        AutomationGenerationRequest request) {

                String systemPrompt = PromptLoader.load(PROMPT);

                String prompt = buildPrompt(request);

                AIRequest aiRequest = new AIRequest(
                                systemPrompt,
                                prompt);

                AIResponse response = provider.complete(aiRequest);

                if (!response.isSuccessful()) {
                        throw new IllegalStateException(
                                        "Automation generation failed: "
                                                        + response.getErrorMessage());
                }

                return new AutomationGenerationResponse(
                                response.getContent());
        }

        private String buildPrompt(
                        AutomationGenerationRequest request) {

                return """
                                User Story:
                                %s

                                Platform:
                                %s

                                Screen:
                                %s

                                Acceptance Criteria:
                                %s

                                Target Package:
                                %s

                                Verified Locator Context:
                                %s
                                """.formatted(
                                request.getUserStory(),
                                request.getPlatform(),
                                request.getScreenName(),
                                request.getAcceptanceCriteria(),
                                request.getTargetPackage(),
                                request.getLocatorContext());
        }
}
