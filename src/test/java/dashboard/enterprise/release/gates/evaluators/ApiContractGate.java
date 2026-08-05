package dashboard.enterprise.release.gates.evaluators;

import com.fasterxml.jackson.databind.JsonNode;
import dashboard.enterprise.release.gates.QualityGateContext;
import dashboard.enterprise.release.gates.QualityGateEvaluator;
import dashboard.enterprise.release.gates.QualityGatePolicy;
import dashboard.enterprise.release.gates.QualityGateResult;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ApiContractGate implements QualityGateEvaluator {

    private static final double MAXIMUM_P95_MS = 500.0;

    private final QualityGatePolicy policy =
            QualityGatePolicy.blocker(
                    "api-contract",
                    "API Contract",
                    "API result PASS; failed steps = 0; P95 <= 500 ms",
                    Map.of(
                            "maximumFailedSteps", 0.0,
                            "maximumP95Ms", MAXIMUM_P95_MS
                    )
            );

    @Override
    public QualityGatePolicy policy() {
        return policy;
    }

    @Override
    public QualityGateResult evaluate(QualityGateContext context) {
        JsonNode summary = context.capability("api");

        if (summary == null || summary.isMissingNode() || summary.isEmpty()) {
            return QualityGateResult.notRun(
                    policy,
                    "API enterprise summary was not published."
            );
        }

        String status = normalize(firstText(
                summary,
                "result",
                "status",
                "overallResult",
                "qualityGate"
        ));

        int failedSteps = integer(
                summary,
                0,
                "failedSteps",
                "failed"
        );

        int assertions = integer(
                summary,
                0,
                "assertions",
                "totalAssertions"
        );

        double p95 = number(
                summary,
                0.0,
                "p95ResponseMillis",
                "p95Ms"
        );

        Map<String, Object> measurements = new LinkedHashMap<>();
        measurements.put("status", status);
        measurements.put("assertions", assertions);
        measurements.put("failedSteps", failedSteps);
        measurements.put("p95ResponseMillis", p95);

        String actual =
                "Status "
                        + status
                        + "; assertions "
                        + assertions
                        + "; failed steps "
                        + failedSteps
                        + "; P95 "
                        + String.format("%.2f ms", p95);

        boolean failed =
                !"PASS".equals(status)
                        || failedSteps > 0
                        || p95 > MAXIMUM_P95_MS;

        if (failed) {
            return QualityGateResult.fail(
                    policy,
                    calculateScore(status, failedSteps, p95),
                    actual,
                    "API contract or response-health thresholds were not satisfied.",
                    "Correct API contract failures or latency breaches before release approval.",
                    measurements,
                    List.of("api/reports/enterprise-summary.json")
            );
        }

        return QualityGateResult.pass(
                policy,
                100,
                actual,
                "API contract assertions and governed response thresholds passed.",
                measurements,
                List.of("api/reports/enterprise-summary.json")
        );
    }

    private int calculateScore(
            String status,
            int failedSteps,
            double p95
    ) {
        int score = 100;

        if (!"PASS".equals(status)) {
            score -= 50;
        }

        score -= Math.min(30, failedSteps * 10);

        if (p95 > MAXIMUM_P95_MS) {
            score -= 25;
        }

        return Math.max(0, score);
    }

    private String firstText(JsonNode node, String... fields) {
        for (String field : fields) {
            JsonNode value = node.get(field);

            if (value != null
                    && !value.isNull()
                    && !value.asText().isBlank()) {
                return value.asText();
            }
        }

        return "NOT_RUN";
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return "NOT_RUN";
        }

        return switch (value.trim().toUpperCase()) {
            case "PASSED", "SUCCESS", "SUCCESSFUL" -> "PASS";
            case "FAILED", "FAILURE", "ERROR" -> "FAIL";
            default -> value.trim().toUpperCase();
        };
    }

    private double number(
            JsonNode node,
            double fallback,
            String... fields
    ) {
        for (String field : fields) {
            JsonNode value = node.get(field);

            if (value != null && value.isNumber()) {
                return value.asDouble();
            }
        }

        return fallback;
    }

    private int integer(
            JsonNode node,
            int fallback,
            String... fields
    ) {
        for (String field : fields) {
            JsonNode value = node.get(field);

            if (value != null && value.isNumber()) {
                return value.asInt();
            }
        }

        return fallback;
    }
}
