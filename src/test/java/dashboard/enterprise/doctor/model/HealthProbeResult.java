package dashboard.enterprise.doctor.model;

import java.util.List;
import java.util.Map;

/**
 * Auditable result produced by a MAPAF Doctor health probe.
 */
public record HealthProbeResult(
        String probeId,
        String probeName,
        String probeVersion,
        HealthSeverity severity,
        HealthStatus status,
        long durationMillis,
        String summary,
        String diagnosis,
        List<String> correctiveActions,
        List<DiagnosticCheck> checks,
        List<String> evidenceReferences,
        Map<String, Object> metadata
) {

    public HealthProbeResult {
        require(probeId, "Probe result id");
        require(probeName, "Probe result name");
        require(probeVersion, "Probe result version");

        if (severity == null) {
            throw new IllegalArgumentException(
                    "Probe result severity is required."
            );
        }

        if (status == null) {
            throw new IllegalArgumentException(
                    "Probe result status is required."
            );
        }

        if (durationMillis < 0) {
            throw new IllegalArgumentException(
                    "Probe duration cannot be negative."
            );
        }

        summary = safe(summary);
        diagnosis = safe(diagnosis);

        correctiveActions = correctiveActions == null
                ? List.of()
                : List.copyOf(correctiveActions);

        checks = checks == null
                ? List.of()
                : List.copyOf(checks);

        evidenceReferences = evidenceReferences == null
                ? List.of()
                : List.copyOf(evidenceReferences);

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public boolean operational() {
        return status.operational();
    }

    public boolean blocksPlatformReadiness() {
        return severity.critical()
                && status == HealthStatus.UNHEALTHY;
    }

    private static void require(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " is required.");
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
