package ai.tests;

import core.ai.analysis.FailureAnalysisParser;
import core.ai.analysis.FailureAnalysisRequest;
import core.ai.analysis.FailureAnalysisResult;
import core.ai.analysis.FailureConfidence;
import core.ai.prompt.PromptRegistry;
import core.ai.prompt.PromptRegistryInitializer;
import core.ai.prompt.PromptRenderer;
import core.ai.providers.AIProvider;
import core.ai.providers.AIRequest;
import core.ai.providers.AIResponse;
import core.ai.services.FailureAnalysisService;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicReference;

public class FailureAnalysisServiceTest {

    @Test
    public void verifyFailureAnalysisServiceCanBeCreated() {

        FailureAnalysisService service =
                new FailureAnalysisService();

        Assert.assertNotNull(service);
    }

    @Test
    public void shouldRenderPromptAndReturnStructuredResult() {

        AtomicReference<AIRequest> capturedRequest =
                new AtomicReference<>();

        AIProvider provider =
                request -> {

                    capturedRequest.set(request);

                    return AIResponse.success("""
                            ## Root Cause

                            The locator no longer matches the element.

                            ## Confidence

                            HIGH

                            ## Recommended Fix

                            Replace the locator with a stable ID.

                            ## Locator Healing

                            YES

                            ## Additional Evidence

                            Capture the latest page source.
                            """);
                };

        FailureAnalysisService service =
                service(provider);

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
                capturedRequest.get()
                        .getSystemMessage()
                        .contains(
                                "expert automation architect"
                        )
        );

        Assert.assertTrue(
                capturedRequest.get()
                        .getPrompt()
                        .contains("Checkout Test")
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

        FailureAnalysisResult result =
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
    public void shouldReturnFailureWhenProviderReturnsNull() {

        AIProvider provider =
                request -> null;

        FailureAnalysisResult result =
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
                    "Failure analysis request must not be null"
    )
    public void shouldRejectNullRequest() {

        service(
                request ->
                        AIResponse.success("unused")
        ).analyze(
                (FailureAnalysisRequest) null
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
                                Review logs
                                """);

        FailureAnalysisResult result =
                service(provider)
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

    private FailureAnalysisService service(
            AIProvider provider) {

        PromptRegistry registry =
                PromptRegistryInitializer
                        .createDefaultRegistry();

        return new FailureAnalysisService(
                provider,
                registry,
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
}