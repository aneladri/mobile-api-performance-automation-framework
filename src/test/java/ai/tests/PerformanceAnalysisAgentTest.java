package ai.tests;

import core.ai.PerformanceAnalysisAgent;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PerformanceAnalysisAgentTest {

    @Test
    public void verifyPerformanceAgentDetectsRegression() {

        PerformanceAnalysisAgent agent =
                new PerformanceAnalysisAgent();

        String report =
                agent.analyze("performance-regression");

        Assert.assertTrue(
                report.contains("Performance Impact")
        );

        Assert.assertTrue(
                report.contains("REGRESSION")
        );

        Assert.assertTrue(
                report.contains("Current p95")
        );
    }
}
