package ai.tests;

import core.ai.AgentResponse;
import core.ai.FailureAnalysisRuntime;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FailureAnalysisRuntimeTest {

    @Test
    public void verifySslFailureAnalysis() {

        FailureAnalysisRuntime runtime =
                new FailureAnalysisRuntime();

        AgentResponse response =
                runtime.analyze(
                        "SSLHandshakeException"
                );

        Assert.assertEquals(
                response.getClassification(),
                "SSL Configuration Failure"
        );

        Assert.assertEquals(
                response.getConfidence(),
                95
        );
    }
}
