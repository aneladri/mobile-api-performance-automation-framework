package dashboard.enterprise.doctor.executive.model;

import dashboard.enterprise.doctor.model.HealthSeverity;
import dashboard.enterprise.doctor.model.HealthStatus;

public record ExecutiveProbeSummary(
        String probeId,
        String name,
        HealthStatus status,
        HealthSeverity severity,
        int score,
        boolean operational,
        boolean blocking,
        String summary,
        String diagnosis,
        long durationMillis
) {

    public ExecutiveProbeSummary {
        probeId = safe(probeId);
        name = safe(name);
        summary = safe(summary);
        diagnosis = safe(diagnosis);

        if (status == null) {
            throw new IllegalArgumentException(
                    "Executive probe status is required."
            );
        }

        if (severity == null) {
            throw new IllegalArgumentException(
                    "Executive probe severity is required."
            );
        }

        if (score < 0 || score > 100) {
            throw new IllegalArgumentException(
                    "Executive probe score must be between 0 and 100."
            );
        }

        if (durationMillis < 0) {
            throw new IllegalArgumentException(
                    "Executive probe duration cannot be negative."
            );
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
