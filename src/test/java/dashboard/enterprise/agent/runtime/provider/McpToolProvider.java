package dashboard.enterprise.agent.runtime.provider;

import dashboard.enterprise.agent.model.AgentContext;
import dashboard.enterprise.agent.runtime.mcp.*;
import dashboard.enterprise.agent.runtime.model.*;

import java.util.*;

public final class McpToolProvider implements ToolProvider {
    private final McpServerDefinition server;
    private final List<ToolCapability> capabilities;

    public McpToolProvider(McpServerDefinition server, List<ToolCapability> capabilities) {
        this.server = server;
        this.capabilities = List.copyOf(capabilities);
    }

    public String providerId() { return server.serverId(); }
    public ProviderType providerType() { return ProviderType.MCP; }
    public int priority() { return 70; }
    public ProviderConnection connection() { return new ProviderConnection("mapaf.tool.connection/v1", server.serverId() + "-connection", server.endpoint(), Map.of("transport", server.transport())); }
    public List<ToolCapability> capabilities() { return capabilities; }

    public ToolInvocationResult invoke(AgentContext context, CapabilityRequest request) {
        McpRequest mcpRequest = new McpRequest("mapaf.mcp.request/v1", context.correlationId(), request.capabilityId(), request.parameters());
        McpResponse response = new McpResponse("mapaf.mcp.response/v1", mcpRequest.requestId(), true, "MCP replay invocation completed.", Map.of("serverId", server.serverId(), "method", mcpRequest.method()));
        return new ToolInvocationResult("mapaf.tool.invocation/v1", server.serverId(), request.capabilityId(), response.successful(), response.summary(), List.of("mcp:" + server.serverId()), response.result());
    }
}
