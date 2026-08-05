package dashboard.enterprise.release;

import dashboard.enterprise.release.decision.ExecutiveDecision;
import dashboard.enterprise.release.gates.QualityGateResult;

import java.util.List;
import java.util.Map;

public record ReleaseReadinessSnapshot(
        String schemaVersion,
        String generatedAt,
        String application,
        String environment,
        String overallStatus,
        int readinessScore,
        String releaseRisk,
        int evidenceCoveragePercent,
        int aiReadinessPercent,
        int businessReadinessPercent,
        String recommendation,
        Map<String, QualityGate> qualityGates,
        List<String> decisionDrivers,
        int blockingGatesPassed,
        int blockingGatesTotal,
        int advisoryGatesPassed,
        int advisoryGatesTotal,
        boolean releaseBlocked,
        List<QualityGateResult> qualityGateResults,
        ExecutiveDecision executiveDecision
) {

    public ReleaseReadinessSnapshot(
            String schemaVersion,
            String generatedAt,
            String application,
            String environment,
            String overallStatus,
            int readinessScore,
            String releaseRisk,
            int evidenceCoveragePercent,
            int aiReadinessPercent,
            int businessReadinessPercent,
            String recommendation,
            Map<String, QualityGate> qualityGates,
            List<String> decisionDrivers
    ) {
        this(
                schemaVersion,
                generatedAt,
                application,
                environment,
                overallStatus,
                readinessScore,
                releaseRisk,
                evidenceCoveragePercent,
                aiReadinessPercent,
                businessReadinessPercent,
                recommendation,
                qualityGates,
                decisionDrivers,
                0,
                0,
                0,
                0,
                false,
                List.of(),
                null
        );
    }

    public ReleaseReadinessSnapshot {
        qualityGates = qualityGates == null
                ? Map.of()
                : Map.copyOf(qualityGates);

        decisionDrivers = decisionDrivers == null
                ? List.of()
                : List.copyOf(decisionDrivers);

        qualityGateResults = qualityGateResults == null
                ? List.of()
                : List.copyOf(qualityGateResults);
    }

    public record QualityGate(
            String capability,
            String status,
            int score,
            String rationale
    ) {
    }
}
