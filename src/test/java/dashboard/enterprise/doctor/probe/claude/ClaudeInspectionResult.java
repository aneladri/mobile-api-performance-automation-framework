package dashboard.enterprise.doctor.probe.claude;

import java.util.List;
import java.util.Map;

public record ClaudeInspectionResult(
        boolean providerEnabled,
        boolean apiKeyConfigured,
        boolean endpointConfigured,
        boolean liveConnectivityChecked,
        boolean liveConnectivityHealthy,
        boolean governedReplayAvailable,
        int governedAssets,
        String provider,
        String endpoint,
        String diagnosis,
        long durationMillis,
        List<String> evidenceReferences,
        Map<String, Object> metadata
) {

    public ClaudeInspectionResult {
        provider = safe(provider);
        endpoint = safe(endpoint);
        diagnosis = safe(diagnosis);

        evidenceReferences = evidenceReferences == null
                ? List.of()
                : List.copyOf(evidenceReferences);

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);

        if (durationMillis < 0) {
            throw new IllegalArgumentException(
                    "Claude inspection duration cannot be negative."
            );
        }
    }

    public boolean liveProviderHealthy() {
        return providerEnabled
                && apiKeyConfigured
                && endpointConfigured
                && (!liveConnectivityChecked
                || liveConnectivityHealthy);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
