package ai.tests;

import core.ai.AgentMetrics;
import core.ai.AgentMetricsCollector;
import core.ai.PerformanceAnalysisAgent;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PerformanceAgentMetricsTest {

    @BeforeMethod
    public void resetMetrics() {
        AgentMetricsCollector.reset();
    }

    @Test
    public void verifyPerformanceAgentMetricsAreCollected() {

        PerformanceAnalysisAgent agent =
                new PerformanceAnalysisAgent();

        agent.analyze(
                "performance-regression"
        );

        AgentMetrics metrics =
                AgentMetricsCollector.getMetrics(
                        "performance-analysis"
                );

        Assert.assertNotNull(metrics);

        Assert.assertEquals(
                metrics.getTotalRuns(),
                1
        );

        Assert.assertTrue(
                metrics.getAverageConfidence() > 0
        );
    }
}