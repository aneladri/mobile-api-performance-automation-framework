package dashboard.enterprise.aggregation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import dashboard.enterprise.model.EnterpriseModuleView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class EnterpriseSummaryAdapter {

    private static final ObjectMapper MAPPER = JsonMapper.getInstance();

    public EnterpriseModuleView read(String key, String displayName, Path path) {
        if (path == null || !Files.isRegularFile(path)) {
            return notRun(key, displayName, path);
        }

        try {
            JsonNode root = MAPPER.readTree(path.toFile());

            String rawStatus = firstText(
                    root,
                    "result",
                    "status",
                    "overallResult",
                    "qualityGate"
            );
            String status = normalizeStatus(rawStatus);

            int total = firstInteger(
                    root,
                    0,
                    "steps",
                    "totalSteps",
                    "apiCalls",
                    "requests"
            );
            int passed = firstInteger(root, total, "passedSteps", "passed");
            int failed = firstInteger(root, 0, "failedSteps", "failed");
            double successRate = firstDecimal(
                    root,
                    total == 0 ? 0.0 : passed * 100.0 / total,
                    "successRate"
            );

            long durationMillis = firstLong(
                    root,
                    0L,
                    "totalDurationMillis",
                    "durationMillis"
            );
            if (durationMillis == 0L) {
                double durationSeconds = firstDecimal(
                        root,
                        0.0,
                        "durationSeconds",
                        "totalDurationSeconds"
                );
                durationMillis = Math.round(durationSeconds * 1000.0);
            }

            Map<String, Object> metrics = new LinkedHashMap<>();
            addIfPresent(metrics, root, "assertions", "Assertions");
            addIfPresent(metrics, root, "screenshots", "Screenshots");
            addIfPresent(metrics, root, "coveragePercent", "Capture Coverage");
            addIfPresent(metrics, root, "trackingConfidence", "Tracking Confidence");
            addIfPresent(metrics, root, "finalWorkflowState", "Workflow State");
            addIfPresent(metrics, root, "browser", "Browser");
            addIfPresent(metrics, root, "locatorConfidence", "Locator Confidence");
            addIfPresent(metrics, root, "domStability", "DOM Stability");
            addIfPresent(metrics, root, "averageResponseMillis", "Average Response");
            addIfPresent(metrics, root, "p95ResponseMillis", "P95 Response");
            addIfPresent(metrics, root, "averageMs", "Average Response");
            addIfPresent(metrics, root, "p95Ms", "P95 Response");
            addIfPresent(metrics, root, "p99Ms", "P99 Response");
            addIfPresent(metrics, root, "throughput", "Throughput");
            addIfPresent(metrics, root, "throughputPerSecond", "Throughput");
            addIfPresent(metrics, root, "availability", "Availability");
            addIfPresent(metrics, root, "availabilityPercent", "Availability");
            addIfPresent(metrics, root, "errorRate", "Error Rate");
            addIfPresent(metrics, root, "errorRatePercent", "Error Rate");
            addIfPresent(metrics, root, "requests", "Requests");
            addIfPresent(metrics, root, "qualityGate", "Quality Gate");
            addIfPresent(metrics, root, "recommendation", "Recommendation");
            addIfPresent(metrics, root, "aiRisk", "AI Risk");

            JsonNode diagnostics = root.path("diagnostics");
            if (diagnostics.isObject()) {
                addIfPresent(metrics, diagnostics, "deviceName", "Device");
                addIfPresent(metrics, diagnostics, "platform", "Platform");
                addIfPresent(metrics, diagnostics, "accessibility", "Accessibility");
                addIfPresent(metrics, diagnostics, "networkFailures", "Network Failures");
                addIfPresent(metrics, diagnostics, "pageErrors", "Page Errors");
                addIfPresent(metrics, diagnostics, "traceStatus", "Trace");
                addIfPresent(metrics, diagnostics, "videoStatus", "Video");
            }

            Map<String, String> links = new LinkedHashMap<>();
            links.put("Open detailed report", key.toLowerCase() + ".html");
            links.put("Enterprise summary", relativeLink(path));
            addDefaultReportLinks(key, links);

            return new EnterpriseModuleView(
                    key,
                    displayName,
                    status,
                    text(root, "scenario", displayName + " validation"),
                    text(root, "environment", "QA"),
                    successRate,
                    durationMillis,
                    total,
                    passed,
                    failed,
                    metrics,
                    links
            );
        } catch (IOException exception) {
            Map<String, Object> metrics = new LinkedHashMap<>();
            metrics.put("Read Error", exception.getMessage());
            return new EnterpriseModuleView(
                    key,
                    displayName,
                    "NOT_RUN",
                    "Summary could not be read",
                    "N/A",
                    0.0,
                    0L,
                    0,
                    0,
                    0,
                    metrics,
                    Map.of()
            );
        }
    }

    private EnterpriseModuleView notRun(String key, String displayName, Path path) {
        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("Message", "Run the enterprise demo to publish this module");
        if (path != null) {
            metrics.put("Expected Summary", path.toString());
        }

        return new EnterpriseModuleView(
                key,
                displayName,
                "NOT_RUN",
                displayName + " has not been executed",
                "N/A",
                0.0,
                0L,
                0,
                0,
                0,
                metrics,
                Map.of()
        );
    }

    private void addDefaultReportLinks(String key, Map<String, String> links) {
        switch (key.toLowerCase()) {
            case "mobile" -> links.put("Mobile evidence", "../../mobile/reports/");
            case "web" -> links.put("Playwright artifacts", "../../web/artifacts/");
            case "api" -> links.put("API report", "../../api/reports/");
            case "performance" -> links.put("Performance dashboard", "../../performance/reports/index.html");
            default -> {
            }
        }
    }

    private String relativeLink(Path path) {
        return "../../" + path.toString().replace('\\', '/');
    }

    private void addIfPresent(Map<String, Object> metrics, JsonNode root, String field, String label) {
        JsonNode value = root.get(field);
        if (value == null || value.isNull() || value.isMissingNode()) {
            return;
        }
        metrics.put(label, value.isValueNode() ? value.asText() : value.toString());
    }

    private String text(JsonNode node, String field, String fallback) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? fallback : value.asText(fallback);
    }

    private String firstText(JsonNode node, String... fields) {
        for (String field : fields) {
            JsonNode value = node.get(field);
            if (value != null && !value.isNull() && !value.asText().isBlank()) {
                return value.asText();
            }
        }
        return "NOT_RUN";
    }

    private int firstInteger(JsonNode node, int fallback, String... fields) {
        for (String field : fields) {
            JsonNode value = node.get(field);
            if (value != null && value.isNumber()) {
                return value.asInt();
            }
        }
        return fallback;
    }

    private long firstLong(JsonNode node, long fallback, String... fields) {
        for (String field : fields) {
            JsonNode value = node.get(field);
            if (value != null && value.isNumber()) {
                return value.asLong();
            }
        }
        return fallback;
    }

    private double firstDecimal(JsonNode node, double fallback, String... fields) {
        for (String field : fields) {
            JsonNode value = node.get(field);
            if (value != null && value.isNumber()) {
                return value.asDouble();
            }
        }
        return fallback;
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return "NOT_RUN";
        }

        return switch (status.trim().toUpperCase()) {
            case "PASS", "PASSED", "SUCCESS", "SUCCESSFUL" -> "PASS";
            case "FAIL", "FAILED", "FAILURE", "ERROR" -> "FAIL";
            case "PARTIAL", "WARNING" -> "PARTIAL";
            case "SKIPPED" -> "SKIPPED";
            default -> "NOT_RUN";
        };
    }
}
