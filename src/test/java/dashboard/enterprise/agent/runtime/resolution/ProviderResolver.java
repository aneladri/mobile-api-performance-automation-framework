package dashboard.enterprise.agent.runtime.resolution;

import dashboard.enterprise.agent.runtime.model.CapabilityRequest;
import dashboard.enterprise.agent.runtime.provider.ToolProvider;
import dashboard.enterprise.agent.runtime.registry.ProviderRegistry;

import java.util.Comparator;

public final class ProviderResolver {
    private final ProviderRegistry registry;

    public ProviderResolver(ProviderRegistry registry) { this.registry = registry; }

    public ToolProvider resolve(CapabilityRequest request) {
        if (!request.preferredProviderId().isBlank()) {
            ToolProvider preferred = registry.require(request.preferredProviderId());
            if (!preferred.supports(request.capabilityId())) throw new IllegalArgumentException("Preferred provider does not support capability: " + request.capabilityId());
            return preferred;
        }
        return registry.all().stream()
                .filter(provider -> provider.supports(request.capabilityId()))
                .sorted(Comparator.comparingInt(ToolProvider::priority).reversed().thenComparing(ToolProvider::providerId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No provider supports capability: " + request.capabilityId()));
    }
}
