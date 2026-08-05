package dashboard.enterprise.intelligence.trend.model;

import dashboard.enterprise.doctor.model.HealthStatus;

public record TrendPoint(
        String recordId,
        String capturedAt,
        String releaseId,
        String executionId,
        HealthStatus overallStatus,
        int healthScore,
        boolean platformReady,
        int healthyProbes,
        int degradedProbes,
        int unhealthyProbes,
        int notConfiguredProbes
) {
    public TrendPoint {
        recordId = safe(recordId);
        capturedAt = safe(capturedAt);
        releaseId = safe(releaseId);
        executionId = safe(executionId);
        if (overallStatus == null) {
            throw new IllegalArgumentException("Trend point status is required.");
        }
        if (healthScore < 0 || healthScore > 100) {
            throw new IllegalArgumentException("Trend point score must be between 0 and 100.");
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
