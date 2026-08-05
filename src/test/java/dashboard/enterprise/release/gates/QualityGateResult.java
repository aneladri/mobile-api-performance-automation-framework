package dashboard.enterprise.release.gates;

import java.util.List;
import java.util.Map;

/**
 * Auditable result produced by a quality-gate evaluator.
 */
public record QualityGateResult(
        String gateId,
        String gateName,
        String policyVersion,
        GateSeverity severity,
        GateStatus status,
        int score,
        boolean releaseBlocking,
        String threshold,
        String actual,
        String rationale,
        String recommendation,
        Map<String, Object> measurements,
        List<String> evidenceReferences
) {

    public QualityGateResult {
        if (gateId == null || gateId.isBlank()) {
            throw new IllegalArgumentException("Gate result id is required.");
        }

        if (gateName == null || gateName.isBlank()) {
            throw new IllegalArgumentException(
                    "Gate result name is required."
            );
        }

        if (severity == null) {
            throw new IllegalArgumentException(
                    "Gate result severity is required."
            );
        }

        if (status == null) {
            throw new IllegalArgumentException(
                    "Gate result status is required."
            );
        }

        if (score < 0 || score > 100) {
            throw new IllegalArgumentException(
                    "Gate score must be between 0 and 100."
            );
        }

        threshold = safe(threshold);
        actual = safe(actual);
        rationale = safe(rationale);
        recommendation = safe(recommendation);

        measurements = measurements == null
                ? Map.of()
                : Map.copyOf(measurements);

        evidenceReferences = evidenceReferences == null
                ? List.of()
                : List.copyOf(evidenceReferences);
    }

    public static QualityGateResult pass(
            QualityGatePolicy policy,
            int score,
            String actual,
            String rationale,
            Map<String, Object> measurements,
            List<String> evidence
    ) {
        return create(
                policy,
                GateStatus.PASS,
                score,
                actual,
                rationale,
                "No release action required.",
                measurements,
                evidence
        );
    }

    public static QualityGateResult fail(
            QualityGatePolicy policy,
            int score,
            String actual,
            String rationale,
            String recommendation,
            Map<String, Object> measurements,
            List<String> evidence
    ) {
        return create(
                policy,
                GateStatus.FAIL,
                score,
                actual,
                rationale,
                recommendation,
                measurements,
                evidence
        );
    }

    public static QualityGateResult notRun(
            QualityGatePolicy policy,
            String rationale
    ) {
        return create(
                policy,
                GateStatus.NOT_RUN,
                0,
                "No qualifying evidence was published.",
                rationale,
                policy.severity().blocksRelease()
                        ? "Run the required capability before release approval."
                        : "Publish the required evidence before final approval.",
                Map.of(),
                List.of()
        );
    }

    private static QualityGateResult create(
            QualityGatePolicy policy,
            GateStatus status,
            int score,
            String actual,
            String rationale,
            String recommendation,
            Map<String, Object> measurements,
            List<String> evidence
    ) {
        boolean blocked = policy.enabled()
                && policy.severity().blocksRelease()
                && status.failed();

        return new QualityGateResult(
                policy.id(),
                policy.name(),
                policy.policyVersion(),
                policy.severity(),
                status,
                score,
                blocked,
                policy.thresholdDescription(),
                actual,
                rationale,
                recommendation,
                measurements,
                evidence
        );
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
