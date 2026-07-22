package ai.tests;

import core.ai.HealingRecommendation;
import core.ai.SelfHealingRuntime;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SelfHealingRuntimeTest {

    @Test
    public void verifySelfHealingRecommendationForLocatorFailure() {

        SelfHealingRuntime runtime =
                new SelfHealingRuntime();

        HealingRecommendation recommendation =
                runtime.analyze(
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
    public void verifySelfHealingSummaryGeneration() {

        SelfHealingRuntime runtime =
                new SelfHealingRuntime();

        String summary =
                runtime.generateFixSummary(
                        "TimeoutException: element not visible"
                );

        Assert.assertTrue(
                summary.contains("Synchronization Failure")
        );

        Assert.assertTrue(
                summary.contains("explicit wait")
        );
    }
}
