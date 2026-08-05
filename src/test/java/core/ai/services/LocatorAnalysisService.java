package core.ai.services;

import core.ai.locator.LocatorAnalysisParser;
import core.ai.locator.LocatorAnalysisRequest;
import core.ai.locator.LocatorAnalysisResult;
import core.ai.locator.LocatorCandidate;
import core.ai.locator.LocatorRankingEngine;
import core.ai.prompt.PromptDefinition;
import core.ai.prompt.PromptRegistry;
import core.ai.prompt.PromptRegistryInitializer;
import core.ai.prompt.PromptRenderer;
import core.ai.prompt.PromptVariables;
import core.ai.prompt.RenderedPrompt;
import core.ai.prompt.templates.LocatorAnalysisPrompt;
import core.ai.providers.AIProvider;
import core.ai.providers.AIProviderFactory;
import core.ai.providers.AIRequest;
import core.ai.providers.AIResponse;

import java.util.List;
import java.util.Objects;

/**
 * Orchestrates structured AI locator analysis.
 */
public final class LocatorAnalysisService {

    private final AIProvider provider;
    private final PromptRegistry registry;
    private final PromptRenderer renderer;
    private final LocatorAnalysisParser parser;
    private final LocatorRankingEngine rankingEngine;

    public LocatorAnalysisService() {
        this(
                AIProviderFactory.getProvider(),
                PromptRegistryInitializer.createDefaultRegistry(),
                new PromptRenderer(),
                new LocatorAnalysisParser(),
                new LocatorRankingEngine()
        );
    }

    public LocatorAnalysisService(
            AIProvider provider,
            PromptRegistry registry,
            PromptRenderer renderer,
            LocatorAnalysisParser parser,
            LocatorRankingEngine rankingEngine) {

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
                "Locator analysis parser must not be null"
        );

        this.rankingEngine = Objects.requireNonNull(
                rankingEngine,
                "Locator ranking engine must not be null"
        );
    }

    /**
     * Performs structured locator analysis using the versioned
     * prompt framework.
     */
    public LocatorAnalysisResult analyze(
            LocatorAnalysisRequest request) {

        Objects.requireNonNull(
                request,
                "Locator analysis request must not be null"
        );

        PromptDefinition definition =
                registry.getLatest(
                        LocatorAnalysisPrompt.NAME
                );

        RenderedPrompt renderedPrompt =
                renderer.render(
                        definition,
                        toPromptVariables(request)
                );

        AIResponse response =
                provider.complete(
                        new AIRequest(
                                renderedPrompt.getSystemMessage(),
                                renderedPrompt.getUserMessage()
                        )
                );

        if (response == null) {
            return LocatorAnalysisResult.failure(
                    "AI provider returned no response"
            );
        }

        if (!response.isSuccessful()) {
            return LocatorAnalysisResult.failure(
                    safeErrorMessage(
                            response.getErrorMessage()
                    )
            );
        }

        LocatorAnalysisResult parsedResult =
                parser.parse(
                        response.getContent()
                );

        if (!parsedResult.isSuccessful()) {
            return parsedResult;
        }

        List<LocatorCandidate> rankedCandidates =
                rankingEngine.rank(
                        parsedResult.getCandidates()
                );

        return LocatorAnalysisResult.builder()
                .successful(true)
                .candidates(rankedCandidates)
                .summary(parsedResult.getSummary())
                .rawResponse(parsedResult.getRawResponse())
                .build();
    }

    private PromptVariables toPromptVariables(
            LocatorAnalysisRequest request) {

        return PromptVariables.builder()
                .put(
                        "platform",
                        request.getPlatform()
                )
                .put(
                        "screenName",
                        request.getScreenName()
                )
                .put(
                        "elementDescription",
                        request.getElementDescription()
                )
                .put(
                        "existingLocator",
                        request.getExistingLocator()
                )
                .put(
                        "failureMessage",
                        request.getFailureMessage()
                )
                .put(
                        "pageSource",
                        request.getPageSource()
                )
                .build();
    }

    private String safeErrorMessage(
            String value) {

        if (value == null || value.isBlank()) {
            return "AI locator analysis request failed";
        }

        return value;
    }
}
