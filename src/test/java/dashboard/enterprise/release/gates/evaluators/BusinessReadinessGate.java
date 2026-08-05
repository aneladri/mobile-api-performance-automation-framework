package dashboard.enterprise.release.gates.evaluators;

import com.fasterxml.jackson.databind.JsonNode;
import dashboard.enterprise.release.gates.QualityGateContext;
import dashboard.enterprise.release.gates.QualityGateEvaluator;
import dashboard.enterprise.release.gates.QualityGatePolicy;
import dashboard.enterprise.release.gates.QualityGateResult;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class BusinessReadinessGate implements QualityGateEvaluator {

    private final QualityGatePolicy policy =
            QualityGatePolicy.blocker(
                    "business-readiness",
                    "Business Readiness",
                    "Mobile, Portal, API, and Performance business outcomes must pass",
                    Map.of("minimumPassedCapabilities", 4.0)
            );

    @Override
    public QualityGatePolicy policy() {
        return policy;
    }

    @Override
    public QualityGateResult evaluate(QualityGateContext context) {
        List<String> modules = List.of(
                "mobile",
                "web",
                "api",
                "performance"
        );

        Map<String, Object> measurements = new LinkedHashMap<>();
        int passed = 0;

        for (String module : modules) {
            JsonNode summary = context.capability(module);
            String status = normalize(firstText(
                    summary,
                    "result",
                    "status",
                    "overallResult",
                    "qualityGate"
            ));

            measurements.put(module + "Status", status);

            if ("PASS".equals(status)) {
                passed++;
            }
        }

        int score = (int) Math.round(
                passed * 100.0 / modules.size()
        );

        measurements.put("passedCapabilities", passed);
        measurements.put(
                "requiredCapabilities",
                modules.size()
        );

        String actual =
                passed + "/" + modules.size()
                        + " business capabilities passed";

        if (passed != modules.size()) {
            return QualityGateResult.fail(
                    policy,
                    score,
                    actual,
                    "One or more release-critical business capabilities did not pass.",
                    "Resolve the failed business capability before approving the release.",
                    measurements,
                    List.of(
                            "mobile/reports/enterprise-summary.json",
                            "web/reports/enterprise-summary.json",
                            "api/reports/enterprise-summary.json",
                            "performance/reports/enterprise-summary.json"
                    )
            );
        }

        return QualityGateResult.pass(
                policy,
                100,
                actual,
                "All release-critical business capabilities passed.",
                measurements,
                List.of(
                        "mobile/reports/enterprise-summary.json",
                        "web/reports/enterprise-summary.json",
                        "api/reports/enterprise-summary.json",
                        "performance/reports/enterprise-summary.json"
                )
        );
    }

    private String firstText(
            JsonNode node,
            String... fields
    ) {
        if (node == null) {
            return "NOT_RUN";
        }

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
}
