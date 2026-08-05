package dashboard.enterprise.release.gates.evaluators;

import dashboard.enterprise.release.gates.GateSeverity;
import dashboard.enterprise.release.gates.GateStatus;
import dashboard.enterprise.release.gates.QualityGateContext;
import dashboard.enterprise.release.gates.QualityGateEvaluator;
import dashboard.enterprise.release.gates.QualityGatePolicy;
import dashboard.enterprise.release.gates.QualityGateResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class EvidenceCompletenessGate implements QualityGateEvaluator {

    private static final double MINIMUM_COVERAGE = 90.0;

    private final QualityGatePolicy policy =
            new QualityGatePolicy(
                    "evidence-completeness",
                    "Evidence Completeness",
                    "1.0",
                    GateSeverity.HIGH,
                    true,
                    20,
                    "Required evidence coverage >= 90%",
                    Map.of("minimumCoveragePercent", MINIMUM_COVERAGE)
            );

    @Override
    public QualityGatePolicy policy() {
        return policy;
    }

    @Override
    public QualityGateResult evaluate(QualityGateContext context) {
        Map<String, Boolean> availability =
                context.evidenceAvailability();

        if (availability.isEmpty()) {
            return QualityGateResult.notRun(
                    policy,
                    "No evidence availability inventory was published."
            );
        }

        List<String> missing = new ArrayList<>();
        List<String> evidence = new ArrayList<>();
        Map<String, Object> measurements = new LinkedHashMap<>();

        int available = 0;

        for (Map.Entry<String, Boolean> entry : availability.entrySet()) {
            measurements.put(entry.getKey(), entry.getValue());

            if (Boolean.TRUE.equals(entry.getValue())) {
                available++;
                evidence.add(entry.getKey());
            } else {
                missing.add(entry.getKey());
            }
        }

        int total = availability.size();
        int coverage = total == 0
                ? 0
                : (int) Math.round(available * 100.0 / total);

        measurements.put("availableEvidence", available);
        measurements.put("totalEvidence", total);
        measurements.put("coveragePercent", coverage);

        String actual =
                coverage + "% coverage; "
                        + available + "/" + total
                        + " evidence assets available";

        if (coverage < MINIMUM_COVERAGE) {
            return new QualityGateResult(
                    policy.id(),
                    policy.name(),
                    policy.policyVersion(),
                    policy.severity(),
                    GateStatus.WARN,
                    coverage,
                    false,
                    policy.thresholdDescription(),
                    actual,
                    "Required evidence coverage is below the governed threshold.",
                    "Publish the missing evidence assets before final release approval.",
                    measurements,
                    evidence
            );
        }

        return QualityGateResult.pass(
                policy,
                coverage,
                actual,
                "Required release evidence is complete.",
                measurements,
                evidence
        );
    }
}
