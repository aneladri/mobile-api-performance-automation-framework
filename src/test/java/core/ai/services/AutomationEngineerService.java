package core.ai.services;

import core.ai.PromptLoader;
import core.ai.models.AutomationGenerationParser;
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

        AIResponse response = provider.complete(
                new AIRequest(systemPrompt, prompt)
        );

        if (!response.isSuccessful()) {
            throw new IllegalStateException(
                    "Automation generation failed: "
                            + response.getErrorMessage()
            );
        }

        return AutomationGenerationParser.parse(response.getContent());
    }

    private String buildPrompt(AutomationGenerationRequest request) {
        return """
                AUTOMATION GENERATION REQUEST

                User Story:
                %s

                Platform:
                %s

                Primary Screen:
                %s

                Acceptance Criteria:
                %s

                Target Package:
                %s

                MANDATORY REQUIREMENTS

                1. Use the Target Package exactly as supplied in every Java file.
                2. Put all Screen Object classes inside one SCREEN_OBJECT section.
                3. Put one Business Flow class inside BUSINESS_FLOW.
                4. Put one TestNG test class inside TEST_CLASS.
                5. Return all six required section markers exactly once and in order.
                6. Do not use Markdown, code fences, explanations, or README content.
                7. Generate one public top-level Java type per source file.
                8. Never invent locators. Use UNRESOLVED_LOCATOR when necessary.
                9. Do not use TODO_ prefixes or TODO/FIXME comments in Java.
                10. Ensure every Java class is complete and has balanced braces.

                Verified Locator Context:
                %s
                """.formatted(
                request.getUserStory(),
                request.getPlatform(),
                request.getScreenName(),
                request.getAcceptanceCriteria(),
                request.getTargetPackage(),
                request.getLocatorContext()
        );
    }
}
