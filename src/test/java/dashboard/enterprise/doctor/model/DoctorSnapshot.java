package dashboard.enterprise.doctor.model;

import java.util.List;

/**
 * Versioned output contract for MAPAF Doctor.
 */
public record DoctorSnapshot(
        String schemaVersion,
        String generatedAt,
        String product,
        String platformVersion,
        String environment,
        HealthStatus overallStatus,
        int healthScore,
        boolean platformReady,
        int totalProbes,
        int healthyProbes,
        int degradedProbes,
        int unhealthyProbes,
        int notConfiguredProbes,
        String executiveSummary,
        List<String> platformDiagnoses,
        List<String> correctiveActions,
        List<HealthProbeResult> probeResults
) {

    public DoctorSnapshot {
        schemaVersion = safe(schemaVersion);
        generatedAt = safe(generatedAt);
        product = safe(product);
        platformVersion = safe(platformVersion);
        environment = safe(environment);
        executiveSummary = safe(executiveSummary);

        if (overallStatus == null) {
            throw new IllegalArgumentException(
                    "Doctor overall status is required."
            );
        }

        if (healthScore < 0 || healthScore > 100) {
            throw new IllegalArgumentException(
                    "Doctor health score must be between 0 and 100."
            );
        }

        platformDiagnoses = platformDiagnoses == null
                ? List.of()
                : List.copyOf(platformDiagnoses);

        correctiveActions = correctiveActions == null
                ? List.of()
                : List.copyOf(correctiveActions);

        probeResults = probeResults == null
                ? List.of()
                : List.copyOf(probeResults);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
