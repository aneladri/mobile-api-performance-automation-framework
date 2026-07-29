package ai.tests;

import core.ai.locator.LocatorAnalysisParser;
import core.ai.locator.LocatorAnalysisRequest;
import core.ai.locator.LocatorAnalysisResult;
import core.ai.locator.LocatorCandidate;
import core.ai.locator.LocatorRankingEngine;
import core.ai.locator.LocatorStrategy;
import core.ai.prompt.PromptRegistry;
import core.ai.prompt.PromptRegistryInitializer;
import core.ai.prompt.PromptRenderer;
import core.ai.providers.AIProvider;
import core.ai.providers.AIRequest;
import core.ai.providers.AIResponse;
import core.ai.services.LocatorAnalysisService;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicReference;

public class LocatorAnalysisServiceTest {

    @Test
    public void verifyLocatorAnalysisServiceCanBeCreated() {

        LocatorAnalysisService service =
                new LocatorAnalysisService();

        Assert.assertNotNull(service);
    }

    @Test
    public void shouldRenderPromptAndReturnRankedLocatorResult() {

        AtomicReference<AIRequest> capturedRequest =
                new AtomicReference<>();

        AIProvider provider =
                request -> {

                    capturedRequest.set(request);

                    return AIResponse.success("""
                            ## Summary

                            Accessibility ID is the preferred locator.

                            ## Candidates

                            Strategy: XPATH
                            Value: //button[@text='Submit']
                            Confidence: 99
                            Fallback: false
                            Reasoning: Text-based XPath is less stable.

                            Strategy: ACCESSIBILITY_ID
                            Value: submit-order
                            Confidence: 90
                            Fallback: false
                            Reasoning: Stable accessibility identifier.

                            Strategy: RESOURCE_ID
                            Value: com.example:id/submit_order
                            Confidence: 95
                            Fallback: true
                            Reasoning: Stable native identifier.
                            """);
                };

        LocatorAnalysisService service =
                service(provider);

        LocatorAnalysisResult result =
                service.analyze(
                        completeRequest()
                );

        Assert.assertTrue(
                result.isSuccessful()
        );

        Assert.assertEquals(
                result.getCandidates().size(),
                3
        );

        LocatorCandidate bestCandidate =
                result.getBestCandidate();

        Assert.assertNotNull(
                bestCandidate
        );

        Assert.assertEquals(
                bestCandidate.getStrategy(),
                LocatorStrategy.ACCESSIBILITY_ID
        );

        Assert.assertEquals(
                bestCandidate.getValue(),
                "submit-order"
        );

        Assert.assertEquals(
                bestCandidate.getConfidence(),
                90
        );

        Assert.assertFalse(
                bestCandidate.isFallback()
        );

        Assert.assertEquals(
                result.getSummary(),
                "Accessibility ID is the preferred locator."
        );

        Assert.assertNotNull(
                capturedRequest.get()
        );

        Assert.assertTrue(
                capturedRequest.get()
                        .getSystemMessage()
                        .contains(
                                "expert automation architect"
                        )
        );

        Assert.assertTrue(
                capturedRequest.get()
                        .getSystemMessage()
                        .contains(
                                "## Candidates"
                        )
        );

        Assert.assertTrue(
                capturedRequest.get()
                        .getPrompt()
                        .contains("Android")
        );

        Assert.assertTrue(
                capturedRequest.get()
                        .getPrompt()
                        .contains("Checkout")
        );

        Assert.assertTrue(
                capturedRequest.get()
                        .getPrompt()
                        .contains("Submit order button")
        );

        Assert.assertTrue(
                capturedRequest.get()
                        .getPrompt()
                        .contains(
                                "xpath=//button[@text='Submit']"
                        )
        );

        Assert.assertTrue(
                capturedRequest.get()
                        .getPrompt()
                        .contains(
                                "NoSuchElementException"
                        )
        );

        Assert.assertTrue(
                capturedRequest.get()
                        .getPrompt()
                        .contains("submit-order")
        );

        Assert.assertFalse(
                capturedRequest.get()
                        .getPrompt()
                        .contains("{{")
        );
    }

    @Test
    public void shouldReturnFailureWhenProviderFails() {

        AIProvider provider =
                request ->
                        AIResponse.failure(
                                "Claude integration is disabled"
                        );

        LocatorAnalysisResult result =
                service(provider)
                        .analyze(
                                completeRequest()
                        );

        Assert.assertFalse(
                result.isSuccessful()
        );

        Assert.assertEquals(
                result.getErrorMessage(),
                "Claude integration is disabled"
        );
    }

    @Test
    public void shouldUseDefaultErrorWhenProviderFailureMessageIsBlank() {

        AIProvider provider =
                request ->
                        AIResponse.failure(" ");

        LocatorAnalysisResult result =
                service(provider)
                        .analyze(
                                completeRequest()
                        );

        Assert.assertFalse(
                result.isSuccessful()
        );

        Assert.assertEquals(
                result.getErrorMessage(),
                "AI locator analysis request failed"
        );
    }

    @Test
    public void shouldReturnFailureWhenProviderReturnsNull() {

        AIProvider provider =
                request -> null;

        LocatorAnalysisResult result =
                service(provider)
                        .analyze(
                                completeRequest()
                        );

        Assert.assertFalse(
                result.isSuccessful()
        );

        Assert.assertEquals(
                result.getErrorMessage(),
                "AI provider returned no response"
        );
    }

    @Test(
            expectedExceptions =
                    NullPointerException.class,
            expectedExceptionsMessageRegExp =
                    "Locator analysis request must not be null"
    )
    public void shouldRejectNullRequest() {

        service(
                request ->
                        AIResponse.success("unused")
        ).analyze(
                (LocatorAnalysisRequest) null
        );
    }

    @Test
    public void shouldReturnParserFailureForMalformedResponse() {

        AIProvider provider =
                request ->
                        AIResponse.success("""
                                ## Summary

                                No candidate could be identified.
                                """);

        LocatorAnalysisResult result =
                service(provider)
                        .analyze(
                                completeRequest()
                        );

        Assert.assertFalse(
                result.isSuccessful()
        );

        Assert.assertEquals(
                result.getErrorMessage(),
                "Locator analysis response is missing candidates"
        );
    }

    @Test
    public void shouldSkipInvalidCandidateAndReturnRankedValidCandidates() {

        AIProvider provider =
                request ->
                        AIResponse.success("""
                                ## Summary

                                Mixed locator response.

                                ## Candidates

                                Strategy: ID
                                Value:
                                Confidence: 95
                                Fallback: false
                                Reasoning: Missing locator value.

                                Strategy: XPATH
                                Value: //button[@text='Submit']
                                Confidence: 100
                                Fallback: false
                                Reasoning: XPath candidate.

                                Strategy: RESOURCE_ID
                                Value: com.example:id/submit_order
                                Confidence: 70
                                Fallback: false
                                Reasoning: Stable native identifier.
                                """);

        LocatorAnalysisResult result =
                service(provider)
                        .analyze(
                                completeRequest()
                        );

        Assert.assertTrue(
                result.isSuccessful()
        );

        Assert.assertEquals(
                result.getCandidates().size(),
                2
        );

        Assert.assertEquals(
                result.getBestCandidate().getStrategy(),
                LocatorStrategy.RESOURCE_ID
        );
    }

    private LocatorAnalysisService service(
            AIProvider provider) {

        PromptRegistry registry =
                PromptRegistryInitializer
                        .createDefaultRegistry();

        return new LocatorAnalysisService(
                provider,
                registry,
                new PromptRenderer(),
                new LocatorAnalysisParser(),
                new LocatorRankingEngine()
        );
    }

    private LocatorAnalysisRequest completeRequest() {

        return LocatorAnalysisRequest.builder()
                .platform("Android")
                .screenName("Checkout")
                .elementDescription(
                        "Submit order button"
                )
                .existingLocator(
                        "xpath=//button[@text='Submit']"
                )
                .failureMessage(
                        "NoSuchElementException"
                )
                .pageSource("""
                        <hierarchy>
                            <node
                                resource-id="com.example:id/submit_order"
                                content-desc="submit-order"
                                text="Submit"/>
                        </hierarchy>
                        """)
                .build();
    }
}
