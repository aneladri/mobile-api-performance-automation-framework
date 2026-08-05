package dashboard.enterprise.release.gates.evaluators;

import com.fasterxml.jackson.databind.JsonNode;
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

public final class AiReadinessGate implements QualityGateEvaluator {

    private static final double MINIMUM_CONFIDENCE = 85.0;

    private final QualityGatePolicy policy =
            new QualityGatePolicy(
                    "ai-readiness",
                    "AI Readiness",
                    "1.0",
                    GateSeverity.ADVISORY,
                    true,
                    15,
                    "Average AI confidence >= 85%; diagnosis and recommendation available",
                    Map.of("minimumConfidencePercent", MINIMUM_CONFIDENCE)
            );

    @Override
    public QualityGatePolicy policy() {
        return policy;
    }

    @Override
    public QualityGateResult evaluate(QualityGateContext context) {
        List<Double> confidenceValues = new ArrayList<>();
        List<String> evidence = new ArrayList<>();
        List<String> missingNarratives = new ArrayList<>();
        Map<String, Object> measurements = new LinkedHashMap<>();

        for (String module : List.of(
                "mobile",
                "web",
                "api",
                "performance"
        )) {
            JsonNode intelligence = context.intelligence(module);

            if (intelligence == null
                    || intelligence.isMissingNode()
                    || intelligence.isEmpty()) {
                missingNarratives.add(module + " intelligence");
                continue;
            }

            double confidence = confidence(intelligence);
            String diagnosis = text(intelligence, "diagnosis");
            String recommendation = text(
                    intelligence,
                    "recommendation"
            );

            measurements.put(
                    module + "Confidence",
                    confidence
            );

            if (confidence > 0) {
                confidenceValues.add(confidence);
            }

            if (diagnosis.isBlank() || recommendation.isBlank()) {
                missingNarratives.add(module + " narrative");
            }

            evidence.add(
                    module + "/reports/failure-showcase.json"
            );
        }

        if (confidenceValues.isEmpty()) {
            return QualityGateResult.notRun(
                    policy,
                    "No AI confidence evidence was published."
            );
        }

        double average = confidenceValues.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        measurements.put("averageConfidence", average);
        measurements.put(
                "missingNarratives",
                missingNarratives.size()
        );

        String actual =
                "Average confidence "
                        + String.format("%.2f%%", average)
                        + "; missing narratives "
                        + missingNarratives.size();

        if (average < MINIMUM_CONFIDENCE
                || !missingNarratives.isEmpty()) {
            return new QualityGateResult(
                    policy.id(),
                    policy.name(),
                    policy.policyVersion(),
                    policy.severity(),
                    GateStatus.WARN,
                    Math.max(0, (int) Math.round(average)),
                    false,
                    policy.thresholdDescription(),
                    actual,
                    "AI confidence or decision narratives require review.",
                    "Review AI diagnostics and obtain human approval before relying on the recommendation.",
                    measurements,
                    evidence
            );
        }

        return QualityGateResult.pass(
                policy,
                (int) Math.round(average),
                actual,
                "AI diagnosis, confidence, and recommendations satisfy the advisory policy.",
                measurements,
                evidence
        );
    }

    private double confidence(JsonNode node) {
        JsonNode value = node.get("confidence");

        if (value == null || value.isNull()) {
            return 0.0;
        }

        if (value.isNumber()) {
            return value.asDouble();
        }

        String digits = value.asText().replaceAll("[^0-9.]", "");
        return digits.isBlank() ? 0.0 : Double.parseDouble(digits);
    }

    private String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull()
                ? ""
                : value.asText("");
    }
}
