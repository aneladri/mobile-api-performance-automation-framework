package dashboard.enterprise.release.gates.evaluators;

import com.fasterxml.jackson.databind.JsonNode;
import dashboard.enterprise.release.gates.QualityGateContext;
import dashboard.enterprise.release.gates.QualityGateEvaluator;
import dashboard.enterprise.release.gates.QualityGatePolicy;
import dashboard.enterprise.release.gates.QualityGateResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class FunctionalQualityGate implements QualityGateEvaluator {

    private static final double MINIMUM_SUCCESS_RATE = 95.0;

    private final QualityGatePolicy policy =
            QualityGatePolicy.blocker(
                    "functional-quality",
                    "Functional Quality",
                    "All required capabilities executed; success rate >= 95%; failed critical steps = 0",
                    Map.of(
                            "minimumSuccessRate", MINIMUM_SUCCESS_RATE,
                            "maximumFailedCriticalSteps", 0.0
                    )
            );

    @Override
    public QualityGatePolicy policy() {
        return policy;
    }

    @Override
    public QualityGateResult evaluate(QualityGateContext context) {
        List<String> modules = List.of("mobile", "web", "api");
        List<String> failures = new ArrayList<>();
        List<String> evidence = new ArrayList<>();
        Map<String, Object> measurements = new LinkedHashMap<>();

        double lowestSuccessRate = 100.0;
        int totalFailedSteps = 0;

        for (String module : modules) {
            JsonNode summary = context.capability(module);

            if (summary == null || summary.isMissingNode() || summary.isEmpty()) {
                failures.add(module + " summary not published");
                continue;
            }

            evidence.add(module + "/reports/enterprise-summary.json");

            String status = normalize(firstText(
                    summary,
                    "result",
                    "status",
                    "overallResult",
                    "qualityGate"
            ));

            double successRate = number(
                    summary,
                    100.0,
                    "successRate",
                    "successRatePercent"
            );

            int failedSteps = integer(
                    summary,
                    0,
                    "failedSteps",
                    "failed",
                    "failedTransactions"
            );

            measurements.put(module + "Status", status);
            measurements.put(module + "SuccessRate", successRate);
            measurements.put(module + "FailedSteps", failedSteps);

            lowestSuccessRate = Math.min(lowestSuccessRate, successRate);
            totalFailedSteps += failedSteps;

            if (!"PASS".equals(status)) {
                failures.add(module + " status=" + status);
            }

            if (successRate < MINIMUM_SUCCESS_RATE) {
                failures.add(
                        module + " success rate=" + successRate + "%"
                );
            }

            if (failedSteps > 0) {
                failures.add(
                        module + " failed steps=" + failedSteps
                );
            }
        }

        measurements.put("lowestSuccessRate", lowestSuccessRate);
        measurements.put("totalFailedSteps", totalFailedSteps);

        String actual =
                "Lowest success rate "
                        + String.format("%.2f%%", lowestSuccessRate)
                        + "; failed steps "
                        + totalFailedSteps;

        if (!failures.isEmpty()) {
            int score = Math.max(
                    0,
                    (int) Math.round(lowestSuccessRate)
                            - Math.min(50, totalFailedSteps * 10)
            );

            return QualityGateResult.fail(
                    policy,
                    score,
                    actual,
                    String.join("; ", failures),
                    "Resolve failed functional workflows before release approval.",
                    measurements,
                    evidence
            );
        }

        return QualityGateResult.pass(
                policy,
                100,
                actual,
                "All required functional capabilities passed governed quality thresholds.",
                measurements,
                evidence
        );
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
