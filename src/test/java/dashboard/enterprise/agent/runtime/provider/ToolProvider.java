package dashboard.enterprise.agent.runtime.provider;

import dashboard.enterprise.agent.model.AgentContext;
import dashboard.enterprise.agent.runtime.model.*;

import java.util.List;

public interface ToolProvider {
    String SCHEMA_VERSION = "mapaf.tool.provider/v1";

    String providerId();
    ProviderType providerType();
    int priority();
    ProviderConnection connection();
    List<ToolCapability> capabilities();
    ToolInvocationResult invoke(AgentContext context, CapabilityRequest request) throws Exception;

    default boolean supports(String capabilityId) {
        return capabilities().stream().anyMatch(capability -> capability.capabilityId().equals(capabilityId));
    }
}
