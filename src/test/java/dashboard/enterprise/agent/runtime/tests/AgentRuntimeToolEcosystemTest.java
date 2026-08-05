package dashboard.enterprise.agent.runtime.tests;

import dashboard.enterprise.agent.model.AgentContext;
import dashboard.enterprise.agent.runtime.mcp.McpServerDefinition;
import dashboard.enterprise.agent.runtime.model.*;
import dashboard.enterprise.agent.runtime.provider.*;
import dashboard.enterprise.agent.runtime.registry.InMemoryProviderRegistry;
import dashboard.enterprise.agent.runtime.resolution.*;
import dashboard.enterprise.agent.tool.PermissionLevel;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.*;

public final class AgentRuntimeToolEcosystemTest {

    @Test
    public void shouldResolveHighestPriorityProviderByCapability() throws Exception {
        var registry = new InMemoryProviderRegistry();
        registry.register(replay("replay-jira", 40, "work-item.read", PermissionLevel.READ_ONLY, Map.of("workItem", "RS-101")));
        registry.register(replay("replay-azure", 80, "work-item.read", PermissionLevel.READ_ONLY, Map.of("workItem", "501")));
        ToolInvocationResult result = new CapabilityRuntime(new ProviderResolver(registry)).invoke(
                context(), new CapabilityRequest("mapaf.tool.invocation/v1", "work-item.read", "", Map.of()), true
        );
        Assert.assertEquals(result.providerId(), "replay-azure");
        Assert.assertEquals(result.data().get("workItem"), "501");
    }

    @Test
    public void shouldHonorPreferredProvider() throws Exception {
        var registry = new InMemoryProviderRegistry();
        registry.register(replay("replay-jira", 40, "work-item.read", PermissionLevel.READ_ONLY, Map.of("workItem", "RS-101")));
        registry.register(replay("replay-azure", 80, "work-item.read", PermissionLevel.READ_ONLY, Map.of("workItem", "501")));
        ToolInvocationResult result = new CapabilityRuntime(new ProviderResolver(registry)).invoke(
                context(), new CapabilityRequest("mapaf.tool.invocation/v1", "work-item.read", "replay-jira", Map.of()), true
        );
        Assert.assertEquals(result.providerId(), "replay-jira");
    }

    @Test(expectedExceptions = SecurityException.class)
    public void shouldRequireApprovalForWriteCapability() throws Exception {
        var registry = new InMemoryProviderRegistry();
        registry.register(replay("pipeline-replay", 100, "pipeline.trigger", PermissionLevel.WRITE_WITH_APPROVAL, Map.of("status", "QUEUED")));
        new CapabilityRuntime(new ProviderResolver(registry)).invoke(
                context(), new CapabilityRequest("mapaf.tool.invocation/v1", "pipeline.trigger", "", Map.of()), false
        );
    }

    @Test
    public void shouldInvokeMcpProviderContract() throws Exception {
        var capability = capability("knowledge.search", PermissionLevel.READ_ONLY);
        var server = new McpServerDefinition("mapaf.mcp.server/v1", "knowledge-mcp", "stdio", "mcp://knowledge", List.of("knowledge.search"), Map.of());
        var registry = new InMemoryProviderRegistry();
        registry.register(new McpToolProvider(server, List.of(capability)));
        ToolInvocationResult result = new CapabilityRuntime(new ProviderResolver(registry)).invoke(
                context(), new CapabilityRequest("mapaf.tool.invocation/v1", "knowledge.search", "", Map.of("text", "RoomScan upload")), true
        );
        Assert.assertTrue(result.successful());
        Assert.assertEquals(result.providerId(), "knowledge-mcp");
        Assert.assertEquals(result.data().get("method"), "knowledge.search");
    }

    private static ReplayToolProvider replay(String id, int priority, String capabilityId, PermissionLevel permission, Map<String, Object> output) {
        return new ReplayToolProvider(id, priority, List.of(capability(capabilityId, permission)), Map.of(capabilityId, output));
    }

    private static ToolCapability capability(String id, PermissionLevel permission) {
        return new ToolCapability("mapaf.tool.capability/v1", id, id, id, permission);
    }

    private static AgentContext context() {
        return new AgentContext(Path.of("."), "RUNTIME-1", "demo-user", Map.of());
    }
}
