package dashboard.enterprise.release.gates.evaluators;

import com.fasterxml.jackson.databind.JsonNode;
import dashboard.enterprise.release.gates.QualityGateContext;
import dashboard.enterprise.release.gates.QualityGateEvaluator;
import dashboard.enterprise.release.gates.QualityGatePolicy;
import dashboard.enterprise.release.gates.QualityGateResult;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PerformanceSlaGate implements QualityGateEvaluator {

    private static final double MAXIMUM_P95_MS = 500.0;
    private static final double MAXIMUM_ERROR_RATE = 1.0;
    private static final double MINIMUM_AVAILABILITY = 99.0;

    private final QualityGatePolicy policy =
            QualityGatePolicy.blocker(
                    "performance-sla",
                    "Performance SLA",
                    "P95 <= 500 ms; error rate < 1%; availability >= 99%",
                    Map.of(
                            "maximumP95Ms", MAXIMUM_P95_MS,
                            "maximumErrorRatePercent", MAXIMUM_ERROR_RATE,
                            "minimumAvailabilityPercent", MINIMUM_AVAILABILITY
                    )
            );

    @Override
    public QualityGatePolicy policy() {
        return policy;
    }

    @Override
    public QualityGateResult evaluate(QualityGateContext context) {
        JsonNode summary = context.capability("performance");

        if (summary == null || summary.isMissingNode() || summary.isEmpty()) {
            return QualityGateResult.notRun(
                    policy,
                    "Performance enterprise summary was not published."
            );
        }

        String status = normalize(firstText(
                summary,
                "result",
                "status",
                "overallResult",
                "qualityGate"
        ));

        double p95 = number(
                summary,
                0.0,
                "p95Ms",
                "p95ResponseMillis"
        );

        double errorRate = number(
                summary,
                0.0,
                "errorRatePercent",
                "errorRate"
        );

        double availability = number(
                summary,
                100.0,
                "availabilityPercent",
                "availability"
        );

        double throughput = number(
                summary,
                0.0,
                "throughputPerSecond",
                "throughput"
        );

        Map<String, Object> measurements = new LinkedHashMap<>();
        measurements.put("status", status);
        measurements.put("p95Ms", p95);
        measurements.put("errorRatePercent", errorRate);
        measurements.put("availabilityPercent", availability);
        measurements.put("throughputPerSecond", throughput);

        String actual =
                "P95 "
                        + String.format("%.2f ms", p95)
                        + "; error rate "
                        + String.format("%.3f%%", errorRate)
                        + "; availability "
                        + String.format("%.2f%%", availability)
                        + "; throughput "
                        + String.format("%.2f req/sec", throughput);

        boolean failed =
                !"PASS".equals(status)
                        || p95 > MAXIMUM_P95_MS
                        || errorRate >= MAXIMUM_ERROR_RATE
                        || availability < MINIMUM_AVAILABILITY;

        if (failed) {
            return QualityGateResult.fail(
                    policy,
                    calculateScore(
                            status,
                            p95,
                            errorRate,
                            availability
                    ),
                    actual,
                    "One or more mandatory performance thresholds were breached.",
                    "Resolve the performance SLA breach before approving the release.",
                    measurements,
                    List.of(
                            "performance/reports/enterprise-summary.json",
                            "performance/k6/reports/smoke/index.html",
                            "performance/jmeter/reports/smoke/index.html"
                    )
            );
        }

        return QualityGateResult.pass(
                policy,
                100,
                actual,
                "All governed performance SLA thresholds passed.",
                measurements,
                List.of(
                        "performance/reports/enterprise-summary.json",
                        "performance/k6/reports/smoke/index.html",
                        "performance/jmeter/reports/smoke/index.html"
                )
        );
    }

    private int calculateScore(
            String status,
            double p95,
            double errorRate,
            double availability
    ) {
        int score = 100;

        if (!"PASS".equals(status)) {
            score -= 40;
        }

        if (p95 > MAXIMUM_P95_MS) {
            score -= 25;
        }

        if (errorRate >= MAXIMUM_ERROR_RATE) {
            score -= 20;
        }

        if (availability < MINIMUM_AVAILABILITY) {
            score -= 20;
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
}
