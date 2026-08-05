package ai.tests;

import core.ai.HealingAnalysisAgent;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HealingAnalysisAgentTest {

    @Test
    public void verifyHealingAgentRecommendation() {

        HealingAnalysisAgent agent =
                new HealingAnalysisAgent();

        String output =
                agent.analyze(
                        "NoSuchElementException: Unable to locate element"
                );

        Assert.assertTrue(
                output.contains("Unified Healing Recommendation")
        );

        Assert.assertTrue(
                output.contains("accessibilityId")
        );
    }
}
