package dashboard.enterprise.intelligence.model;

import dashboard.enterprise.doctor.model.HealthStatus;

import java.util.List;

public record DoctorHistoryRecord(
        String schemaVersion,
        String recordId,
        String capturedAt,
        String releaseId,
        String executionId,
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
        List<String> platformDiagnoses,
        List<String> correctiveActions,
        String sourceReport
) {
    public DoctorHistoryRecord {
        schemaVersion = safe(schemaVersion);
        recordId = required(recordId, "History record id is required.");
        capturedAt = required(capturedAt, "History capture timestamp is required.");
        releaseId = safe(releaseId);
        executionId = required(executionId, "History execution id is required.");
        product = safe(product);
        platformVersion = safe(platformVersion);
        environment = safe(environment);
        sourceReport = safe(sourceReport);

        if (overallStatus == null) {
            throw new IllegalArgumentException("History status is required.");
        }
        if (healthScore < 0 || healthScore > 100) {
            throw new IllegalArgumentException("History health score must be between 0 and 100.");
        }
        if (totalProbes < 0 || healthyProbes < 0 || degradedProbes < 0
                || unhealthyProbes < 0 || notConfiguredProbes < 0) {
            throw new IllegalArgumentException("History probe counts cannot be negative.");
        }

        platformDiagnoses = platformDiagnoses == null ? List.of() : List.copyOf(platformDiagnoses);
        correctiveActions = correctiveActions == null ? List.of() : List.copyOf(correctiveActions);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private static String required(String value, String message) {
        String normalized = safe(value).trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return normalized;
    }
}
