package ai.tests;

import core.ai.HealingRecommendation;
import core.ai.SuggestionEngine;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SuggestionEngineTest {

    @Test
    public void verifyLocatorFailureRecommendation() {

        SuggestionEngine engine =
                new SuggestionEngine();

        HealingRecommendation recommendation =
                engine.recommend(
                        "NoSuchElementException: Unable to locate element"
                );

        Assert.assertEquals(
                recommendation.getFailureType(),
                "Locator Failure"
        );

        Assert.assertTrue(
                recommendation.getSuggestedStrategy()
                        .contains("accessibilityId")
        );
    }

    @Test
    public void verifyTimeoutRecommendation() {

        SuggestionEngine engine =
                new SuggestionEngine();

        HealingRecommendation recommendation =
                engine.recommend(
                        "TimeoutException: element not visible"
                );

        Assert.assertEquals(
                recommendation.getFailureType(),
                "Synchronization Failure"
        );

        Assert.assertTrue(
                recommendation.getSuggestedFix()
                        .contains("explicit wait")
        );
    }

    @Test
    public void verifyStaleElementRecommendation() {

        SuggestionEngine engine =
                new SuggestionEngine();

        HealingRecommendation recommendation =
                engine.recommend(
                        "StaleElementReferenceException"
                );

        Assert.assertEquals(
                recommendation.getFailureType(),
                "Stale Element Failure"
        );
    }
}
