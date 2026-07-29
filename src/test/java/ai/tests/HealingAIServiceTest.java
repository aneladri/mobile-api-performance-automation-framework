package ai.tests;

import core.ai.healing.HealedLocatorCandidate;
import core.ai.locator.LocatorAnalysisParser;
import core.ai.locator.LocatorRankingEngine;
import core.ai.prompt.PromptRegistry;
import core.ai.prompt.PromptRegistryInitializer;
import core.ai.prompt.PromptRenderer;
import core.ai.providers.AIProvider;
import core.ai.providers.AIResponse;
import core.ai.services.HealingAIService;
import core.ai.services.LocatorAnalysisService;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HealingAIServiceTest {

    @Test
    public void verifyHealingAIServiceCanBeCreated() {

        HealingAIService service =
                new HealingAIService();

        Assert.assertNotNull(service);
    }

    @Test
    public void shouldReturnBestRankedHealingCandidate() {

        AIProvider provider =
                request ->
                        AIResponse.success("""
                                ## Summary

                                Accessibility ID is preferred.

                                ## Candidates

                                Strategy: XPATH
                                Value: //button[@text='Submit']
                                Confidence: 99
                                Fallback: false
                                Reasoning: Text XPath.

                                Strategy: ACCESSIBILITY_ID
                                Value: submit-order
                                Confidence: 90
                                Fallback: false
                                Reasoning: Stable accessibility identifier.
                                """);

        HealingAIService service =
                service(provider);

        HealedLocatorCandidate candidate =
                service.recommendLocator(
                        By.id("old-submit"),
                        "CheckoutScreen",
                        "submitOrderTest",
                        "android",
                        "<hierarchy content-desc=\"submit-order\"/>"
                );

        Assert.assertNotNull(candidate);

        Assert.assertEquals(
                candidate.getLocatorType(),
                "ACCESSIBILITY_ID"
        );

        Assert.assertEquals(
                candidate.getLocatorValue(),
                "submit-order"
        );

        Assert.assertEquals(
                candidate.getConfidence(),
                90
        );

        Assert.assertEquals(
                candidate.getExplanation(),
                "Stable accessibility identifier."
        );
    }

    @Test
    public void shouldReturnNullWhenLocatorAnalysisFails() {

        AIProvider provider =
                request ->
                        AIResponse.failure(
                                "Claude integration is disabled"
                        );

        HealedLocatorCandidate candidate =
                service(provider)
                        .recommendLocator(
                                By.id("old-submit"),
                                "CheckoutScreen",
                                "submitOrderTest",
                                "android",
                                "<hierarchy/>"
                        );

        Assert.assertNull(candidate);
    }

    @Test
    public void shouldReturnNullForMalformedAIResponse() {

        AIProvider provider =
                request ->
                        AIResponse.success("""
                                ## Summary

                                No valid locator found.
                                """);

        HealedLocatorCandidate candidate =
                service(provider)
                        .recommendLocator(
                                By.id("old-submit"),
                                "CheckoutScreen",
                                "submitOrderTest",
                                "android",
                                "<hierarchy/>"
                        );

        Assert.assertNull(candidate);
    }

    @Test(
            expectedExceptions = NullPointerException.class,
            expectedExceptionsMessageRegExp =
                    "Broken locator must not be null"
    )
    public void shouldRejectNullBrokenLocator() {

        service(
                request ->
                        AIResponse.success("unused")
        ).recommendLocator(
                null,
                "CheckoutScreen",
                "submitOrderTest",
                "android",
                "<hierarchy/>"
        );
    }

    @Test
    public void shouldUseFallbackValuesForMissingContext() {

        AIProvider provider =
                request -> {

                    Assert.assertTrue(
                            request.getPrompt()
                                    .contains("Unknown screen")
                    );

                    Assert.assertTrue(
                            request.getPrompt()
                                    .contains("Unknown test")
                    );

                    Assert.assertTrue(
                            request.getPrompt()
                                    .contains(
                                            "<page-source-unavailable/>"
                                    )
                    );

                    return AIResponse.success("""
                            ## Summary
                            ID candidate found.

                            ## Candidates
                            Strategy: ID
                            Value: submit
                            Confidence: 80
                            Fallback: false
                            Reasoning: Stable identifier.
                            """);
                };

        HealedLocatorCandidate candidate =
                service(provider)
                        .recommendLocator(
                                By.id("old-submit"),
                                null,
                                null,
                                null,
                                null
                        );

        Assert.assertNotNull(candidate);

        Assert.assertEquals(
                candidate.getLocatorType(),
                "ID"
        );
    }

    private HealingAIService service(
            AIProvider provider) {

        PromptRegistry registry =
                PromptRegistryInitializer
                        .createDefaultRegistry();

        LocatorAnalysisService locatorService =
                new LocatorAnalysisService(
                        provider,
                        registry,
                        new PromptRenderer(),
                        new LocatorAnalysisParser(),
                        new LocatorRankingEngine()
                );

        return new HealingAIService(
                locatorService
        );
    }
}