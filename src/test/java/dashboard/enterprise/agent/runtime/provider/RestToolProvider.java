package dashboard.enterprise.agent.runtime.provider;

import dashboard.enterprise.agent.model.AgentContext;
import dashboard.enterprise.agent.runtime.model.*;

import java.util.List;

public final class RestToolProvider implements ToolProvider {
    private final String providerId;
    private final ProviderConnection connection;
    private final List<ToolCapability> capabilities;

    public RestToolProvider(String providerId, ProviderConnection connection, List<ToolCapability> capabilities) {
        this.providerId = providerId;
        this.connection = connection;
        this.capabilities = List.copyOf(capabilities);
    }

    public String providerId() { return providerId; }
    public ProviderType providerType() { return ProviderType.REST; }
    public int priority() { return 50; }
    public ProviderConnection connection() { return connection; }
    public List<ToolCapability> capabilities() { return capabilities; }
    public ToolInvocationResult invoke(AgentContext context, CapabilityRequest request) {
        throw new UnsupportedOperationException("REST live invocation is not enabled in Sprint 5.6 replay foundation.");
    }
}
