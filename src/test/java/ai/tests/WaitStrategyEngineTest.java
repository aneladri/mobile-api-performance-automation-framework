package ai.tests;

import core.ai.WaitStrategyEngine;
import core.ai.WaitStrategyRecommendation;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WaitStrategyEngineTest {

    @Test
    public void verifyTimeoutWaitRecommendation() {

        WaitStrategyEngine engine =
                new WaitStrategyEngine();

        WaitStrategyRecommendation recommendation =
                engine.recommend(
                        "TimeoutException: element not visible"
                );

        Assert.assertEquals(
                recommendation.getFailureType(),
                "Synchronization Failure"
        );

        Assert.assertTrue(
                recommendation.getImplementation()
                        .contains("waitForVisible")
        );

        Assert.assertEquals(
                recommendation.getConfidence(),
                90
        );
    }

    @Test
    public void verifyClickabilityWaitRecommendation() {

        WaitStrategyEngine engine =
                new WaitStrategyEngine();

        WaitStrategyRecommendation recommendation =
                engine.recommend(
                        "element not clickable"
                );

        Assert.assertEquals(
                recommendation.getFailureType(),
                "Clickability Failure"
        );

        Assert.assertTrue(
                recommendation.getImplementation()
                        .contains("waitForClickable")
        );
    }

    @Test
    public void verifyStaleElementWaitRecommendation() {

        WaitStrategyEngine engine =
                new WaitStrategyEngine();

        WaitStrategyRecommendation recommendation =
                engine.recommend(
                        "stale element reference"
                );

        Assert.assertEquals(
                recommendation.getFailureType(),
                "Stale Element Synchronization Failure"
        );
    }
}
