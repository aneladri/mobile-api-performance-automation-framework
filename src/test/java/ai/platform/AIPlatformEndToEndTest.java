package ai.platform;

import core.ai.ArchitectAnalysisAgent;
import core.ai.DocumentationAnalysisAgent;
import core.ai.FailureAnalysisAgent;
import core.ai.PerformanceAnalysisAgent;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AIPlatformEndToEndTest {

    @Test
    public void verifyFailureAnalysisAgentRunsEndToEnd() {

        FailureAnalysisAgent agent =
                new FailureAnalysisAgent();

        String result =
                agent.analyze(
                        "NoSuchElementException: Unable to locate element loginButton"
                );

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isBlank());
        Assert.assertTrue(result.contains("Failure Analysis"));
    }

    @Test
    public void verifyDocumentationAnalysisAgentRunsEndToEnd() {

        DocumentationAnalysisAgent agent =
                new DocumentationAnalysisAgent();

        String result =
                agent.analyze(
                        "README is missing setup instructions"
                );

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isBlank());
    }

    @Test
    public void verifyArchitectureAnalysisAgentRunsEndToEnd() {

        ArchitectAnalysisAgent agent =
                new ArchitectAnalysisAgent();

        String result =
                agent.analyze(
                        "Framework has duplicate responsibility across services"
                );

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isBlank());
    }

    @Test
    public void verifyPerformanceAnalysisAgentRunsEndToEnd() {

        PerformanceAnalysisAgent agent =
                new PerformanceAnalysisAgent();

        String result =
                agent.analyze(
                        "p95 response time increased from 100ms to 180ms"
                );

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isBlank());
    }
}
