package dashboard.enterprise.doctor.probe.performance;

import java.util.List;
import java.util.Map;

public record PerformanceInspectionResult(
        boolean k6Available,
        boolean jmeterAvailable,
        boolean enterpriseSummaryAvailable,
        boolean dashboardAvailable,
        boolean failureShowcaseAvailable,
        boolean productionWorkloadEvidenceAvailable,
        long totalRequests,
        double errorRatePercent,
        double p95LatencyMillis,
        double maxAllowedErrorRatePercent,
        double maxAllowedP95LatencyMillis,
        boolean workloadPassed,
        String diagnosis,
        long durationMillis,
        List<String> evidenceReferences,
        Map<String, Object> metadata
) {

    public PerformanceInspectionResult {
        diagnosis = diagnosis == null ? "" : diagnosis;

        evidenceReferences = evidenceReferences == null
                ? List.of()
                : List.copyOf(evidenceReferences);

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);

        if (totalRequests < 0
                || errorRatePercent < 0
                || p95LatencyMillis < 0
                || maxAllowedErrorRatePercent < 0
                || maxAllowedP95LatencyMillis < 0
                || durationMillis < 0) {
            throw new IllegalArgumentException(
                    "Performance metrics and duration cannot be negative."
            );
        }
    }

    public boolean errorRateHealthy() {
        return errorRatePercent <= maxAllowedErrorRatePercent;
    }

    public boolean latencyHealthy() {
        return p95LatencyMillis <= maxAllowedP95LatencyMillis;
    }

    public boolean toolsAvailable() {
        return k6Available || jmeterAvailable;
    }

    public boolean evidenceComplete() {
        return enterpriseSummaryAvailable
                && dashboardAvailable
                && failureShowcaseAvailable;
    }
}
