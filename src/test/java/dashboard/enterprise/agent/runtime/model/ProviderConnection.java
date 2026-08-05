package dashboard.enterprise.agent.runtime.model;

import java.util.Map;

public record ProviderConnection(
        String schemaVersion,
        String connectionId,
        String endpoint,
        Map<String, String> attributes
) {
    public ProviderConnection {
        schemaVersion = safe(schemaVersion);
        connectionId = required(connectionId, "Connection id");
        endpoint = safe(endpoint);
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    private static String safe(String value) { return value == null ? "" : value.trim(); }
    private static String required(String value, String label) {
        String normalized = safe(value);
        if (normalized.isEmpty()) throw new IllegalArgumentException(label + " is required.");
        return normalized;
    }
}
