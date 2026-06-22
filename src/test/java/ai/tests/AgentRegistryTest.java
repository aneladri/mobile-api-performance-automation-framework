package ai.tests;

import core.ai.AgentRegistry;
import core.ai.FailureAnalysisAgent;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AgentRegistryTest {

    @Test
    public void verifyAgentRegistration() {
        AgentRegistry registry =
                new AgentRegistry();

        registry.register(
                new FailureAnalysisAgent()
        );

        Assert.assertNotNull(
                registry.get("failure-analysis")
        );
    }
}
