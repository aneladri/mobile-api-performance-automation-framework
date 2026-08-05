package dashboard.enterprise.agent.runtime.provider;

import dashboard.enterprise.agent.model.AgentContext;
import dashboard.enterprise.agent.runtime.model.*;
import dashboard.enterprise.agent.tool.*;

import java.util.List;

public final class NativeToolProvider implements ToolProvider {
    private final String providerId;
    private final int priority;
    private final String capabilityId;
    private final AgentTool tool;

    public NativeToolProvider(String providerId, int priority, String capabilityId, AgentTool tool) {
        this.providerId = providerId;
        this.priority = priority;
        this.capabilityId = capabilityId;
        this.tool = tool;
    }

    public String providerId() { return providerId; }
    public ProviderType providerType() { return ProviderType.NATIVE; }
    public int priority() { return priority; }
    public ProviderConnection connection() { return new ProviderConnection("mapaf.tool.connection/v1", providerId + "-connection", "native://" + tool.definition().toolId(), java.util.Map.of()); }
    public List<ToolCapability> capabilities() {
        return List.of(new ToolCapability("mapaf.tool.capability/v1", capabilityId, capabilityId, tool.definition().description(), tool.definition().permissionLevel()));
    }

    public ToolInvocationResult invoke(AgentContext context, CapabilityRequest request) throws Exception {
        ToolResult result = tool.execute(context, new ToolRequest("execute", request.parameters()));
        return new ToolInvocationResult("mapaf.tool.invocation/v1", providerId, capabilityId, result.successful(), result.summary(), result.evidence(), result.data());
    }
}
