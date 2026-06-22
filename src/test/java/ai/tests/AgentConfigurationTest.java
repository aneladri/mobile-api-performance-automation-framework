package ai.tests;

import core.ai.AgentConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AgentConfigurationTest {

    @Test
    public void verifyDefaultAgentConfiguration() {

        AgentConfiguration config =
                new AgentConfiguration();

        Assert.assertTrue(config.isFailureAnalysisEnabled());
        Assert.assertTrue(config.isDocumentationAnalysisEnabled());
        Assert.assertTrue(config.isArchitectureAnalysisEnabled());
        Assert.assertTrue(config.isPerformanceAnalysisEnabled());
    }

    @Test
    public void verifyAgentCanBeDisabled() {

        System.setProperty("ai.architecture.enabled", "false");

        AgentConfiguration config =
                new AgentConfiguration();

        Assert.assertFalse(
                config.isArchitectureAnalysisEnabled()
        );

        System.clearProperty("ai.architecture.enabled");
    }
}
