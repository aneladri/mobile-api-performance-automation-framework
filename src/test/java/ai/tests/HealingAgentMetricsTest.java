package ai.tests;

import core.ai.AgentMetrics;
import core.ai.AgentMetricsCollector;
import core.ai.HealingAnalysisAgent;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class HealingAgentMetricsTest {

    @BeforeMethod
    public void resetMetrics() {
        AgentMetricsCollector.reset();
    }

    @Test
    public void verifyHealingAgentMetricsAreCollected() {

        HealingAnalysisAgent agent =
                new HealingAnalysisAgent();

        agent.analyze(
                "NoSuchElementException: Unable to locate element"
        );

        AgentMetrics metrics =
                AgentMetricsCollector.getMetrics(
                        "healing-analysis"
                );

        Assert.assertNotNull(metrics);
        Assert.assertEquals(metrics.getTotalRuns(), 1);
        Assert.assertTrue(metrics.getAverageConfidence() > 0);
    }
}
