package ai.platform;

import core.ai.analysis.FailureAnalysisParser;
import core.ai.analysis.FailureAnalysisRequest;
import core.ai.analysis.FailureAnalysisResult;
import core.ai.analysis.FailureConfidence;
import core.ai.prompt.PromptDefinition;
import core.ai.prompt.PromptRegistry;
import core.ai.prompt.PromptRegistryInitializer;
import core.ai.prompt.PromptRenderer;
import core.ai.prompt.templates.FailureAnalysisPrompt;
import core.ai.providers.AIProvider;
import core.ai.providers.AIRequest;
import core.ai.providers.AIResponse;
import core.ai.services.FailureAnalysisService;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicReference;

public class FailureAnalysisIntegrationTest {

    @Test
    public void shouldExecuteCompleteFailureAnalysisPipeline() {

        AtomicReference<AIRequest> capturedRequest =
                new AtomicReference<>();

        AIProvider provider =
                request -> {

                    capturedRequest.set(request);

                    return AIResponse.success("""
                            ## Root Cause

                            The submit button locator no longer matches
                            the current page structure.

                            ## Confidence

                            HIGH

                            ## Recommended Fix

                            Replace the locator with a stable
                            accessibility identifier.

                            ## Locator Healing

                            YES

                            ## Additional Evidence

                            Capture the latest page source and
                            accessibility tree.
                            """);
                };

        FailureAnalysisService service =
                createService(provider);

        FailureAnalysisResult result =
                service.analyze(
                        completeRequest()
                );

        Assert.assertTrue(
                result.isSuccessful()
        );

        Assert.assertEquals(
                result.getConfidence(),
                FailureConfidence.HIGH
        );

        Assert.assertTrue(
                result.isLocatorHealingRecommended()
        );

        Assert.assertTrue(
                result.getRootCause()
                        .contains(
                                "locator no longer matches"
                        )
        );

        Assert.assertTrue(
                result.getRecommendedFix()
                        .contains(
                                "accessibility identifier"
                        )
        );

        Assert.assertNotNull(
                capturedRequest.get()
        );
    }

    @Test
    public void shouldRenderAllFailureEvidenceIntoPrompt() {

        AtomicReference<AIRequest> capturedRequest =
                new AtomicReference<>();

        AIProvider provider =
                request -> {

                    capturedRequest.set(request);

                    return successfulResponse();
                };

        createService(provider)
                .analyze(
                        completeRequest()
                );

        AIRequest request =
                capturedRequest.get();

        Assert.assertNotNull(request);

        String prompt =
                request.getPrompt();

        Assert.assertTrue(
                prompt.contains("Checkout Test")
        );

        Assert.assertTrue(
                prompt.contains("Checkout Page")
        );

        Assert.assertTrue(
                prompt.contains("#submit-order")
        );

        Assert.assertTrue(
                prompt.contains("Element not found")
        );

        Assert.assertTrue(
                prompt.contains(
                        "TimeoutException at CheckoutPage.submit"
                )
        );

        Assert.assertFalse(
                prompt.contains("{{")
        );

        Assert.assertTrue(
                request.getSystemMessage()
                        .contains(
                                "expert automation architect"
                        )
        );
    }

    @Test
    public void shouldReturnFailureWhenProviderFails() {

        AIProvider provider =
                request ->
                        AIResponse.failure(
                                "Claude integration is unavailable"
                        );

        FailureAnalysisResult result =
                createService(provider)
                        .analyze(
                                completeRequest()
                        );

        Assert.assertFalse(
                result.isSuccessful()
        );

        Assert.assertEquals(
                result.getErrorMessage(),
                "Claude integration is unavailable"
        );
    }

    @Test
    public void shouldReturnParserFailureForMalformedResponse() {

        AIProvider provider =
                request ->
                        AIResponse.success("""
                                ## Confidence

                                HIGH

                                ## Recommended Fix

                                Review the service logs.
                                """);

        FailureAnalysisResult result =
                createService(provider)
                        .analyze(
                                completeRequest()
                        );

        Assert.assertFalse(
                result.isSuccessful()
        );

        Assert.assertEquals(
                result.getErrorMessage(),
                "Failure analysis response is missing section: Root Cause"
        );
    }

    @Test
    public void shouldLoadLatestRegisteredFailureAnalysisPrompt() {

        PromptRegistry registry =
                PromptRegistryInitializer
                        .createDefaultRegistry();

        PromptDefinition prompt =
                registry.getLatest(
                        FailureAnalysisPrompt.NAME
                );

        Assert.assertEquals(
                prompt.getName(),
                FailureAnalysisPrompt.NAME
        );

        Assert.assertEquals(
                prompt.getVersion(),
                FailureAnalysisPrompt.VERSION
        );

        Assert.assertEquals(
                prompt.getVersion(),
                "v1"
        );
    }

    @Test
    @SuppressWarnings("deprecation")
    public void shouldPreserveLegacyAnalyzeMethod() {

        AtomicReference<AIRequest> capturedRequest =
                new AtomicReference<>();

        AIProvider provider =
                request -> {

                    capturedRequest.set(request);

                    return AIResponse.success(
                            "Legacy failure analysis response"
                    );
                };

        FailureAnalysisService service =
                createService(provider);

        AIResponse response =
                service.analyze(
                        "TimeoutException: element was not found"
                );

        Assert.assertTrue(
                response.isSuccessful()
        );

        Assert.assertEquals(
                response.getContent(),
                "Legacy failure analysis response"
        );

        Assert.assertNotNull(
                capturedRequest.get()
        );

        Assert.assertTrue(
                capturedRequest.get()
                        .getPrompt()
                        .contains(
                                "TimeoutException"
                        )
        );
    }

    private FailureAnalysisService createService(
            AIProvider provider) {

        return new FailureAnalysisService(
                provider,
                PromptRegistryInitializer
                        .createDefaultRegistry(),
                new PromptRenderer(),
                new FailureAnalysisParser()
        );
    }

    private FailureAnalysisRequest completeRequest() {

        return FailureAnalysisRequest.builder()
                .testName("Checkout Test")
                .testType("UI")
                .pageName("Checkout Page")
                .expectedResult(
                        "Order should be submitted"
                )
                .actualResult(
                        "Submit button was not clicked"
                )
                .locator("#submit-order")
                .errorMessage("Element not found")
                .stackTrace(
                        "TimeoutException at CheckoutPage.submit"
                )
                .build();
    }

    private AIResponse successfulResponse() {

        return AIResponse.success("""
                ## Root Cause

                Locator changed.

                ## Confidence

                HIGH

                ## Recommended Fix

                Replace locator with stable ID.

                ## Locator Healing

                YES

                ## Additional Evidence

                Capture current page source.
                """);
    }
}
