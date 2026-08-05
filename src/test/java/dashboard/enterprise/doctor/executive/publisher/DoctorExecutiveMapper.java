package dashboard.enterprise.doctor.executive.publisher;

import dashboard.enterprise.doctor.executive.model.DoctorExecutiveOverview;
import dashboard.enterprise.doctor.executive.model.DoctorReadinessSummary;
import dashboard.enterprise.doctor.executive.model.ExecutiveProbeSummary;
import dashboard.enterprise.doctor.model.DoctorSnapshot;
import dashboard.enterprise.doctor.model.HealthProbeResult;
import dashboard.enterprise.doctor.model.HealthSeverity;
import dashboard.enterprise.doctor.model.HealthStatus;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class DoctorExecutiveMapper {

    private static final Map<String, Integer> WEIGHTS =
            createWeights();

    public DoctorExecutiveOverview map(
            DoctorSnapshot snapshot
    ) {
        if (snapshot == null) {
            throw new IllegalArgumentException(
                    "Doctor snapshot is required."
            );
        }

        List<ExecutiveProbeSummary> probes =
                snapshot.probeResults()
                        .stream()
                        .map(this::toExecutiveProbe)
                        .toList();

        int weightedScore = weightedScore(
                snapshot.probeResults()
        );

        int blockingIssues = (int) snapshot.probeResults()
                .stream()
                .filter(
                        HealthProbeResult::blocksPlatformReadiness
                )
                .count();

        int warnings = (int) snapshot.probeResults()
                .stream()
                .filter(result ->
                        result.status() == HealthStatus.DEGRADED
                                || result.status()
                                == HealthStatus.NOT_CONFIGURED
                                || (result.status()
                                == HealthStatus.UNHEALTHY
                                && !result.blocksPlatformReadiness()))
                .count();

        List<String> priorityIssues =
                snapshot.probeResults()
                        .stream()
                        .filter(result ->
                                result.status()
                                        != HealthStatus.HEALTHY)
                        .sorted((left, right) ->
                                Integer.compare(
                                        severityRank(
                                                right.severity()
                                        ),
                                        severityRank(
                                                left.severity()
                                        )
                                ))
                        .map(result ->
                                result.probeName()
                                        + ": "
                                        + result.diagnosis())
                        .filter(value -> !value.endsWith(": "))
                        .toList();

        List<String> actions =
                snapshot.correctiveActions()
                        .stream()
                        .distinct()
                        .toList();

        DoctorReadinessSummary readiness =
                readiness(snapshot);

        return new DoctorExecutiveOverview(
                "mapaf.doctor.executive/v1",
                snapshot.generatedAt(),
                snapshot.product(),
                snapshot.platformVersion(),
                snapshot.environment(),
                snapshot.overallStatus(),
                weightedScore,
                snapshot.platformReady(),
                snapshot.totalProbes(),
                snapshot.healthyProbes(),
                snapshot.degradedProbes(),
                snapshot.unhealthyProbes(),
                snapshot.notConfiguredProbes(),
                blockingIssues,
                warnings,
                executiveSummary(
                        snapshot,
                        weightedScore,
                        blockingIssues,
                        warnings
                ),
                readiness,
                probes,
                priorityIssues,
                actions,
                "doctor-report.html"
        );
    }

    private ExecutiveProbeSummary toExecutiveProbe(
            HealthProbeResult result
    ) {
        return new ExecutiveProbeSummary(
                result.probeId(),
                result.probeName(),
                result.status(),
                result.severity(),
                statusScore(result.status()),
                result.operational(),
                result.blocksPlatformReadiness(),
                result.summary(),
                result.diagnosis(),
                result.durationMillis()
        );
    }

    private DoctorReadinessSummary readiness(
            DoctorSnapshot snapshot
    ) {
        return new DoctorReadinessSummary(
                snapshot.platformReady(),
                ready(snapshot, "repository-foundation")
                        && ready(snapshot, "environment-health"),
                ready(snapshot, "dashboard-health"),
                ready(snapshot, "dashboard-health")
                        && ready(snapshot, "api-health")
                        && ready(snapshot, "performance-health"),
                usable(snapshot, "claude-health"),
                usable(snapshot, "device-health"),
                ready(snapshot, "api-health"),
                ready(snapshot, "performance-health")
        );
    }

    private boolean ready(
            DoctorSnapshot snapshot,
            String probeId
    ) {
        return snapshot.probeResults()
                .stream()
                .filter(result ->
                        probeId.equals(result.probeId()))
                .findFirst()
                .map(result ->
                        result.status() == HealthStatus.HEALTHY)
                .orElse(false);
    }

    private boolean usable(
            DoctorSnapshot snapshot,
            String probeId
    ) {
        return snapshot.probeResults()
                .stream()
                .filter(result ->
                        probeId.equals(result.probeId()))
                .findFirst()
                .map(result ->
                        result.status() == HealthStatus.HEALTHY
                                || result.status()
                                == HealthStatus.DEGRADED)
                .orElse(false);
    }

    private int weightedScore(
            List<HealthProbeResult> results
    ) {
        if (results.isEmpty()) {
            return 0;
        }

        int weightedTotal = 0;
        int totalWeight = 0;

        for (HealthProbeResult result : results) {
            int weight = WEIGHTS.getOrDefault(
                    result.probeId(),
                    defaultWeight(result.severity())
            );

            weightedTotal +=
                    statusScore(result.status()) * weight;

            totalWeight += weight;
        }

        if (totalWeight == 0) {
            return 0;
        }

        return (int) Math.round(
                (double) weightedTotal / totalWeight
        );
    }

    private int statusScore(HealthStatus status) {
        return switch (status) {
            case HEALTHY -> 100;
            case DEGRADED -> 70;
            case NOT_CONFIGURED -> 60;
            case UNHEALTHY -> 0;
        };
    }

    private int defaultWeight(
            HealthSeverity severity
    ) {
        return switch (severity) {
            case CRITICAL -> 20;
            case HIGH -> 15;
            case MEDIUM -> 10;
            case LOW -> 5;
            case INFO -> 3;
        };
    }

    private int severityRank(
            HealthSeverity severity
    ) {
        return switch (severity) {
            case CRITICAL -> 5;
            case HIGH -> 4;
            case MEDIUM -> 3;
            case LOW -> 2;
            case INFO -> 1;
        };
    }

    private String executiveSummary(
            DoctorSnapshot snapshot,
            int weightedScore,
            int blockingIssues,
            int warnings
    ) {
        if (blockingIssues > 0) {
            return blockingIssues
                    + " blocking platform issue(s) require "
                    + "immediate remediation.";
        }

        if (warnings > 0) {
            return "MAPAF is operational and platform-ready with "
                    + warnings
                    + " non-blocking warning(s). Weighted health score: "
                    + weightedScore
                    + "%.";
        }

        return "All MAPAF platform capabilities are healthy "
                + "and ready for enterprise execution.";
    }

    private static Map<String, Integer> createWeights() {
        Map<String, Integer> weights =
                new LinkedHashMap<>();

        weights.put("repository-foundation", 15);
        weights.put("product-version", 5);
        weights.put("environment-health", 15);
        weights.put("browser-health", 10);
        weights.put("dashboard-health", 10);
        weights.put("claude-health", 5);
        weights.put("device-health", 10);
        weights.put("api-health", 15);
        weights.put("performance-health", 15);

        return Map.copyOf(weights);
    }
}
