package core.ai.services;

import core.ai.PromptLoader;
import core.ai.analysis.FailureAnalysisParser;
import core.ai.analysis.FailureAnalysisRequest;
import core.ai.analysis.FailureAnalysisResult;
import core.ai.prompt.PromptDefinition;
import core.ai.prompt.PromptRegistry;
import core.ai.prompt.PromptRegistryInitializer;
import core.ai.prompt.PromptRenderer;
import core.ai.prompt.PromptVariables;
import core.ai.prompt.RenderedPrompt;
import core.ai.prompt.templates.FailureAnalysisPrompt;
import core.ai.providers.AIProvider;
import core.ai.providers.AIProviderFactory;
import core.ai.providers.AIRequest;
import core.ai.providers.AIResponse;

import java.util.Objects;

/**
 * Orchestrates structured AI failure analysis.
 */
public final class FailureAnalysisService {

    private static final String LEGACY_PROMPT_NAME =
            "failure-analysis.md";

    private final AIProvider provider;
    private final PromptRegistry registry;
    private final PromptRenderer renderer;
    private final FailureAnalysisParser parser;

    public FailureAnalysisService() {
        this(
                AIProviderFactory.getProvider(),
                PromptRegistryInitializer
                        .createDefaultRegistry(),
                new PromptRenderer(),
                new FailureAnalysisParser()
        );
    }

    public FailureAnalysisService(
            AIProvider provider,
            PromptRegistry registry,
            PromptRenderer renderer,
            FailureAnalysisParser parser) {

        this.provider = Objects.requireNonNull(
                provider,
                "AI provider must not be null"
        );

        this.registry = Objects.requireNonNull(
                registry,
                "Prompt registry must not be null"
        );

        this.renderer = Objects.requireNonNull(
                renderer,
                "Prompt renderer must not be null"
        );

        this.parser = Objects.requireNonNull(
                parser,
                "Failure analysis parser must not be null"
        );
    }

    /**
     * Performs structured failure analysis using the versioned
     * prompt framework.
     */
    public FailureAnalysisResult analyze(
            FailureAnalysisRequest request) {

        Objects.requireNonNull(
                request,
                "Failure analysis request must not be null"
        );

        PromptDefinition definition =
                registry.getLatest(
                        FailureAnalysisPrompt.NAME
                );

        RenderedPrompt renderedPrompt =
                renderer.render(
                        definition,
                        toPromptVariables(request)
                );

        AIResponse response =
                provider.complete(
                        new AIRequest(
                                renderedPrompt
                                        .getSystemMessage(),
                                renderedPrompt
                                        .getUserMessage()
                        )
                );

        if (response == null) {
            return FailureAnalysisResult.failure(
                    "AI provider returned no response"
            );
        }

        if (!response.isSuccessful()) {
            return FailureAnalysisResult.failure(
                    safeErrorMessage(
                            response.getErrorMessage()
                    )
            );
        }

        return parser.parse(
                response.getContent()
        );
    }

    /**
     * Legacy compatibility method used by the existing
     * FailureAnalysisAgent.
     */
    @Deprecated
    public AIResponse analyze(
            String failureContext) {

        String systemPrompt =
                PromptLoader.load(
                        LEGACY_PROMPT_NAME
                );

        AIRequest request =
                new AIRequest(
                        systemPrompt,
                        failureContext
                );

        return provider.complete(request);
    }

    private PromptVariables toPromptVariables(
            FailureAnalysisRequest request) {

        return PromptVariables.builder()
                .put(
                        "testName",
                        request.getTestName()
                )
                .put(
                        "testType",
                        request.getTestType()
                )
                .put(
                        "pageName",
                        request.getPageName()
                )
                .put(
                        "expectedResult",
                        request.getExpectedResult()
                )
                .put(
                        "actualResult",
                        request.getActualResult()
                )
                .put(
                        "locator",
                        request.getLocator()
                )
                .put(
                        "errorMessage",
                        request.getErrorMessage()
                )
                .put(
                        "stackTrace",
                        request.getStackTrace()
                )
                .build();
    }

    private String safeErrorMessage(
            String value) {

        if (value == null || value.isBlank()) {
            return "AI failure analysis request failed";
        }

        return value;
    }
}