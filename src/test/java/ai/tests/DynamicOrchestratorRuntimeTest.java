package ai.tests;

import core.ai.DynamicOrchestratorRuntime;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DynamicOrchestratorRuntimeTest {

    @Test
    public void verifyDynamicOrchestration() {
        DynamicOrchestratorRuntime runtime =
                new DynamicOrchestratorRuntime();

        String report =
                runtime.analyze(
                        "SSLHandshakeException"
                );

        Assert.assertTrue(report.contains("Failure Analysis"));
        Assert.assertTrue(report.contains("Documentation Impact"));
        Assert.assertTrue(report.contains("Architecture Impact"));
        Assert.assertTrue(report.contains("Performance Impact"));
    }
}