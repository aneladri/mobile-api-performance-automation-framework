package dashboard.enterprise.agent.runtime.registry;

import dashboard.enterprise.agent.runtime.provider.ToolProvider;

import java.util.*;

public final class InMemoryProviderRegistry implements ProviderRegistry {
    private final Map<String, ToolProvider> providers = new LinkedHashMap<>();

    public void register(ToolProvider provider) {
        if (provider == null) throw new IllegalArgumentException("Provider is required.");
        if (providers.putIfAbsent(provider.providerId(), provider) != null) throw new IllegalArgumentException("Duplicate provider: " + provider.providerId());
    }

    public ToolProvider require(String providerId) {
        ToolProvider provider = providers.get(providerId);
        if (provider == null) throw new IllegalArgumentException("Unknown provider: " + providerId);
        return provider;
    }

    public Collection<ToolProvider> all() { return List.copyOf(providers.values()); }
}
