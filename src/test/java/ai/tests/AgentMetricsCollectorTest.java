package ai.tests;

import core.ai.AgentMetrics;
import core.ai.AgentMetricsCollector;
import core.ai.FailureAnalysisAgent;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AgentMetricsCollectorTest {

    @BeforeMethod
    public void resetMetrics() {
        AgentMetricsCollector.reset();
    }

    @Test
    public void verifyFailureAnalysisMetricsAreCollected() {

        FailureAnalysisAgent agent =
                new FailureAnalysisAgent();

        agent.analyze("SSLHandshakeException");
        agent.analyze("UNKNOWN FAILURE SAMPLE");

        AgentMetrics metrics =
                AgentMetricsCollector.getMetrics(
                        "failure-analysis"
                );

        Assert.assertEquals(metrics.getTotalRuns(), 2);
        Assert.assertEquals(metrics.getSuccessfulRuns(), 1);
        Assert.assertEquals(metrics.getUnknownClassifications(), 1);
        Assert.assertTrue(metrics.getAverageConfidence() > 0);
    }
}
