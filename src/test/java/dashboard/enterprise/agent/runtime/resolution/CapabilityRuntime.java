package dashboard.enterprise.agent.runtime.resolution;

import dashboard.enterprise.agent.model.AgentContext;
import dashboard.enterprise.agent.runtime.model.*;
import dashboard.enterprise.agent.runtime.provider.ToolProvider;
import dashboard.enterprise.agent.tool.PermissionLevel;

import java.util.Set;

public final class CapabilityRuntime {
    private final ProviderResolver resolver;

    public CapabilityRuntime(ProviderResolver resolver) { this.resolver = resolver; }

    public ToolInvocationResult invoke(AgentContext context, CapabilityRequest request, boolean approved) throws Exception {
        ToolProvider provider = resolver.resolve(request);
        ToolCapability capability = provider.capabilities().stream()
                .filter(item -> item.capabilityId().equals(request.capabilityId()))
                .findFirst()
                .orElseThrow();
        if (capability.permissionLevel() == PermissionLevel.PROHIBITED) throw new SecurityException("Capability is prohibited: " + capability.capabilityId());
        if (capability.permissionLevel() == PermissionLevel.WRITE_WITH_APPROVAL && !approved) throw new SecurityException("Human approval required for capability: " + capability.capabilityId());
        return provider.invoke(context, request);
    }
}
