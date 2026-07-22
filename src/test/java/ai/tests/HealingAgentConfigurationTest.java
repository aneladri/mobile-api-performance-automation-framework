package ai.tests;

import core.ai.AgentConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HealingAgentConfigurationTest {

    @Test
    public void verifyHealingAgentEnabledByDefault() {
        AgentConfiguration config = new AgentConfiguration();

        Assert.assertTrue(
                config.isHealingAnalysisEnabled()
        );
    }

    @Test
    public void verifyHealingAgentCanBeDisabled() {
        System.setProperty("ai.healing.enabled", "false");

        AgentConfiguration config = new AgentConfiguration();

        Assert.assertFalse(
                config.isHealingAnalysisEnabled()
        );

        System.clearProperty("ai.healing.enabled");
    }
}
