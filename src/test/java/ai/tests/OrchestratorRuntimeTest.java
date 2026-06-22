package ai.tests;

import core.ai.OrchestratorRuntime;
import org.testng.Assert;
import org.testng.annotations.Test;

public class OrchestratorRuntimeTest {

    @Test
    public void verifySslOrchestration() {

        OrchestratorRuntime runtime =
                new OrchestratorRuntime();

        String report =
                runtime.analyze(
                        "SSLHandshakeException"
                );

        Assert.assertTrue(
                report.contains(
                        "SSL Configuration Failure"
                )
        );

        Assert.assertTrue(
                report.contains(
                        "Documentation Impact"
                )
        );
    }
}
