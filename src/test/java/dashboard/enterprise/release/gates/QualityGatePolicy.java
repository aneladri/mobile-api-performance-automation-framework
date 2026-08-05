package dashboard.enterprise.release.gates;

import java.util.Map;

/**
 * Versioned policy definition used by a quality-gate evaluator.
 */
public record QualityGatePolicy(
        String id,
        String name,
        String policyVersion,
        GateSeverity severity,
        boolean enabled,
        int weight,
        String thresholdDescription,
        Map<String, Double> numericThresholds
) {

    public QualityGatePolicy {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Gate policy id is required.");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Gate policy name is required.");
        }

        if (policyVersion == null || policyVersion.isBlank()) {
            throw new IllegalArgumentException(
                    "Gate policy version is required."
            );
        }

        if (severity == null) {
            throw new IllegalArgumentException(
                    "Gate policy severity is required."
            );
        }

        if (weight < 0 || weight > 100) {
            throw new IllegalArgumentException(
                    "Gate policy weight must be between 0 and 100."
            );
        }

        thresholdDescription = thresholdDescription == null
                ? ""
                : thresholdDescription;

        numericThresholds = numericThresholds == null
                ? Map.of()
                : Map.copyOf(numericThresholds);
    }

    public static QualityGatePolicy blocker(
            String id,
            String name,
            String thresholdDescription,
            Map<String, Double> thresholds
    ) {
        return new QualityGatePolicy(
                id,
                name,
                "1.0",
                GateSeverity.BLOCKER,
                true,
                100,
                thresholdDescription,
                thresholds
        );
    }
}
