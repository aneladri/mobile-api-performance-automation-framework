package ai.tests;

import core.ai.LocatorRecommendation;
import core.ai.LocatorRecommendationEngine;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LocatorRecommendationEngineTest {

    @Test
    public void verifyNoSuchElementLocatorRecommendation() {

        LocatorRecommendationEngine engine =
                new LocatorRecommendationEngine();

        LocatorRecommendation recommendation =
                engine.recommend(
                        "NoSuchElementException: Unable to locate element"
                );

        Assert.assertEquals(
                recommendation.getFailureType(),
                "Locator Not Found"
        );

        Assert.assertTrue(
                recommendation.getSuggestedLocator()
                        .contains("accessibilityId")
        );

        Assert.assertEquals(
                recommendation.getConfidence(),
                88
        );
    }

    @Test
    public void verifyTimeoutLocatorRecommendation() {

        LocatorRecommendationEngine engine =
                new LocatorRecommendationEngine();

        LocatorRecommendation recommendation =
                engine.recommend(
                        "TimeoutException: element not visible"
                );

        Assert.assertEquals(
                recommendation.getFailureType(),
                "Element Synchronization Issue"
        );

        Assert.assertTrue(
                recommendation.getSuggestedWait()
                        .contains("explicit wait")
        );
    }

    @Test
    public void verifyStaleElementLocatorRecommendation() {

        LocatorRecommendationEngine engine =
                new LocatorRecommendationEngine();

        LocatorRecommendation recommendation =
                engine.recommend(
                        "StaleElementReferenceException"
                );

        Assert.assertEquals(
                recommendation.getFailureType(),
                "Stale Element Reference"
        );

        Assert.assertTrue(
                recommendation.getSuggestedLocator()
                        .contains("Re-locate")
        );
    }
}
