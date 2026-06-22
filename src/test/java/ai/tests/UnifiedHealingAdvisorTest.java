package ai.tests;

import core.ai.UnifiedHealingAdvisor;
import core.ai.UnifiedHealingRecommendation;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UnifiedHealingAdvisorTest {

    @Test
    public void verifyUnifiedRecommendationForLocatorFailure() {

        UnifiedHealingAdvisor advisor =
                new UnifiedHealingAdvisor();

        UnifiedHealingRecommendation recommendation =
                advisor.analyze(
                        "NoSuchElementException: Unable to locate element"
                );

        Assert.assertTrue(
                recommendation.getLocatorRecommendation()
                        .contains("accessibilityId")
        );

        Assert.assertTrue(
                recommendation.getConfidence() > 0
        );
    }

    @Test
    public void verifyUnifiedRecommendationForTimeoutFailure() {

        UnifiedHealingAdvisor advisor =
                new UnifiedHealingAdvisor();

        UnifiedHealingRecommendation recommendation =
                advisor.analyze(
                        "TimeoutException: element not visible"
                );

        Assert.assertTrue(
                recommendation.getWaitRecommendation()
                        .contains("waitForVisible")
        );
    }

    @Test
    public void verifyUnifiedSummaryGeneration() {

        UnifiedHealingAdvisor advisor =
                new UnifiedHealingAdvisor();

        String summary =
                advisor.generateSummary(
                        "ANDROID_HOME is missing"
                );

        Assert.assertTrue(
                summary.contains("Unified Healing Recommendation")
        );

        Assert.assertTrue(
                summary.contains("Android SDK")
        );
    }
}
