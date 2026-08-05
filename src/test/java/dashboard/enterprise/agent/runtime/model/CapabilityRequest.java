package dashboard.enterprise.agent.runtime.model;

import java.util.Map;

public record CapabilityRequest(
        String schemaVersion,
        String capabilityId,
        String preferredProviderId,
        Map<String, Object> parameters
) {
    public CapabilityRequest {
        schemaVersion = schemaVersion == null ? "" : schemaVersion.trim();
        capabilityId = capabilityId == null ? "" : capabilityId.trim();
        preferredProviderId = preferredProviderId == null ? "" : preferredProviderId.trim();
        parameters = parameters == null ? Map.of() : Map.copyOf(parameters);
        if (capabilityId.isEmpty()) throw new IllegalArgumentException("Capability id is required.");
    }
}
