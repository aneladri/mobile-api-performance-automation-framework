package dashboard.enterprise.agent.runtime.registry;

import dashboard.enterprise.agent.runtime.provider.ToolProvider;

import java.util.Collection;

public interface ProviderRegistry {
    void register(ToolProvider provider);
    ToolProvider require(String providerId);
    Collection<ToolProvider> all();
}
