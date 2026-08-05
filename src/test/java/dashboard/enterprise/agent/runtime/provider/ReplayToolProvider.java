package dashboard.enterprise.agent.runtime.provider;

import dashboard.enterprise.agent.model.AgentContext;
import dashboard.enterprise.agent.runtime.model.*;

import java.util.*;

public final class ReplayToolProvider implements ToolProvider {
    private final String providerId;
    private final int priority;
    private final ProviderConnection connection;
    private final List<ToolCapability> capabilities;
    private final Map<String, Map<String, Object>> outputs;

    public ReplayToolProvider(String providerId, int priority, List<ToolCapability> capabilities, Map<String, Map<String, Object>> outputs) {
        this.providerId = providerId;
        this.priority = priority;
        this.capabilities = List.copyOf(capabilities);
        this.outputs = Map.copyOf(outputs);
        this.connection = new ProviderConnection("mapaf.tool.connection/v1", providerId + "-connection", "replay://" + providerId, Map.of("mode", "REPLAY"));
    }

    public String providerId() { return providerId; }
    public ProviderType providerType() { return ProviderType.REPLAY; }
    public int priority() { return priority; }
    public ProviderConnection connection() { return connection; }
    public List<ToolCapability> capabilities() { return capabilities; }

    public ToolInvocationResult invoke(AgentContext context, CapabilityRequest request) {
        if (!supports(request.capabilityId())) throw new IllegalArgumentException("Unsupported capability: " + request.capabilityId());
        Map<String, Object> data = outputs.getOrDefault(request.capabilityId(), Map.of());
        return new ToolInvocationResult(
                "mapaf.tool.invocation/v1",
                providerId,
                request.capabilityId(),
                true,
                "Capability " + request.capabilityId() + " executed by replay provider " + providerId + ".",
                List.of("provider:" + providerId, "capability:" + request.capabilityId()),
                data
        );
    }
}
