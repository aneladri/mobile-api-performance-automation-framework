package dashboard.enterprise.doctor.executive.model;

import dashboard.enterprise.doctor.model.HealthStatus;

import java.util.List;

public record DoctorExecutiveOverview(
        String schemaVersion,
        String generatedAt,
        String product,
        String platformVersion,
        String environment,
        HealthStatus overallStatus,
        int weightedHealthScore,
        boolean platformReady,
        int totalProbes,
        int healthyProbes,
        int degradedProbes,
        int unhealthyProbes,
        int notConfiguredProbes,
        int blockingIssues,
        int warnings,
        String executiveSummary,
        DoctorReadinessSummary readiness,
        List<ExecutiveProbeSummary> probes,
        List<String> priorityIssues,
        List<String> recommendedActions,
        String detailedReport
) {

    public DoctorExecutiveOverview {
        schemaVersion = safe(schemaVersion);
        generatedAt = safe(generatedAt);
        product = safe(product);
        platformVersion = safe(platformVersion);
        environment = safe(environment);
        executiveSummary = safe(executiveSummary);
        detailedReport = safe(detailedReport);

        if (overallStatus == null) {
            throw new IllegalArgumentException(
                    "Executive Doctor status is required."
            );
        }

        if (weightedHealthScore < 0
                || weightedHealthScore > 100) {
            throw new IllegalArgumentException(
                    "Weighted health score must be between 0 and 100."
            );
        }

        if (readiness == null) {
            throw new IllegalArgumentException(
                    "Doctor readiness summary is required."
            );
        }

        probes = probes == null
                ? List.of()
                : List.copyOf(probes);

        priorityIssues = priorityIssues == null
                ? List.of()
                : List.copyOf(priorityIssues);

        recommendedActions = recommendedActions == null
                ? List.of()
                : List.copyOf(recommendedActions);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
