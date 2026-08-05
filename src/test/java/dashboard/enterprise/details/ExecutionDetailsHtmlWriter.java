package dashboard.enterprise.details;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Generates a dedicated engineering report page for each RoomScan capability.
 * Large HTML templates intentionally use token replacement rather than
 * String.formatted() so CSS percentage values are safe.
 */
public final class ExecutionDetailsHtmlWriter {

    private static final ObjectMapper MAPPER = JsonMapper.getInstance();

    public Path write(
            String moduleKey,
            String moduleName,
            Path summaryPath,
            Path outputPath
    ) throws IOException {
        Files.createDirectories(outputPath.toAbsolutePath().getParent());
        JsonNode root = Files.isRegularFile(summaryPath)
                ? MAPPER.readTree(summaryPath.toFile())
                : MAPPER.createObjectNode();
        Files.writeString(
                outputPath,
                render(moduleKey, moduleName, summaryPath, root),
                StandardCharsets.UTF_8
        );
        return outputPath.toAbsolutePath();
    }

    String render(
            String moduleKey,
            String moduleName,
            Path summaryPath,
            JsonNode root
    ) {
        String status = normalize(firstText(root, "result", "status", "overallResult", "qualityGate"));
        String scenario = text(root, "scenario", moduleName + " validation");
        String executionId = text(root, "executionId", "N/A");
        String correlationId = text(root, "correlationId", "N/A");
        String environment = text(root, "environment", "N/A");
        String duration = formatDuration(root);
        String statistics = renderStatistics(moduleKey, root);
        String steps = renderSteps(moduleKey, root);
        String diagnostics = renderNamedObject("Diagnostics", root.path("diagnostics"));
        String engines = renderNamedObject("Engine Comparison", root.path("engines"));
        String quality = renderQualitySection(moduleKey, root);
        String evidence = EvidenceSectionRenderer.render(moduleKey, root);
        String failureShowcase = renderFailureShowcase(moduleKey);
        String transactionBreakdown = renderBusinessTransactions(root.path("businessTransactions"));

        Map<String, String> tokens = new LinkedHashMap<>();
        tokens.put("{{PAGE_TITLE}}", escape(moduleName) + " Execution Report");
        tokens.put("{{MODULE_NAME}}", escape(moduleName));
        tokens.put("{{SCENARIO}}", escape(scenario));
        tokens.put("{{STATUS}}", escape(status));
        tokens.put("{{STATUS_CLASS}}", "PASS".equals(status) ? "pass" : "FAIL".equals(status) ? "fail" : "partial");
        tokens.put("{{EXECUTION_ID}}", escape(executionId));
        tokens.put("{{CORRELATION_ID}}", escape(correlationId));
        tokens.put("{{ENVIRONMENT}}", escape(environment));
        tokens.put("{{DURATION}}", escape(duration));
        tokens.put("{{STATISTICS}}", statistics);
        tokens.put("{{STEPS}}", steps);
        tokens.put("{{DIAGNOSTICS}}", diagnostics);
        tokens.put("{{ENGINES}}", engines);
        tokens.put("{{QUALITY}}", quality);
        tokens.put("{{EVIDENCE}}", evidence);
        tokens.put("{{FAILURE_SHOWCASE}}", failureShowcase);
        tokens.put("{{TRANSACTION_BREAKDOWN}}", transactionBreakdown);
        tokens.put("{{RAW_SUMMARY}}", escape(relativeRawSummary(moduleKey)));
        tokens.put("{{ACTIVE_" + moduleKey.toUpperCase() + "}}", "active");

        String html = template();
        for (Map.Entry<String, String> token : tokens.entrySet()) {
            html = html.replace(token.getKey(), token.getValue());
        }
        return html.replaceAll("\\{\\{ACTIVE_[A-Z]+}}", "");
    }

    private String template() {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>{{PAGE_TITLE}}</title>
                  <style>
                    :root{--orange:#d04a02;--dark:#1d252d;--muted:#667085;--bg:#f4f6f8;--surface:#fff;--green:#147d64;--red:#c62828;--amber:#a15c00;--border:#dfe3e8;}
                    *{box-sizing:border-box}body{margin:0;font-family:Inter,Arial,sans-serif;background:var(--bg);color:var(--dark)}
                    header{background:linear-gradient(115deg,#1d252d,#3b4650);color:#fff;padding:25px 5vw}.brand{color:#ff8f4c;font-size:12px;font-weight:800;letter-spacing:2px;text-transform:uppercase}h1{margin:8px 0 4px}.sub{opacity:.82}
                    nav{position:sticky;top:0;z-index:5;background:#fff;border-bottom:1px solid var(--border);padding:11px 5vw;display:flex;flex-wrap:wrap;gap:8px}nav a{padding:8px 12px;border:1px solid var(--border);border-radius:7px;color:var(--dark);text-decoration:none;font-size:13px;font-weight:750}nav a:hover,nav a.active{border-color:var(--orange);color:var(--orange)}
                    .container{max-width:1500px;margin:auto;padding:26px 5vw 60px}.hero{display:grid;grid-template-columns:repeat(auto-fit,minmax(200px,1fr));gap:14px;margin-bottom:22px}.card,.step{background:var(--surface);border:1px solid var(--border);border-radius:12px;padding:18px;box-shadow:0 4px 14px rgba(16,24,40,.05)}
                    .status{font-size:31px;font-weight:850}.status.pass{color:var(--green)}.status.fail{color:var(--red)}.status.partial{color:var(--amber)}.label{font-size:11px;color:var(--muted);text-transform:uppercase;letter-spacing:1px;font-weight:800}.value{font-size:18px;font-weight:750;margin-top:8px;word-break:break-word}
                    .section{margin-top:25px}.grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(330px,1fr));gap:14px}.step{border-left:5px solid var(--green)}.step.fail{border-left-color:var(--red)}.step-head{display:flex;justify-content:space-between;gap:10px}.badge{padding:4px 9px;border-radius:999px;background:#e6f4ef;color:#075b47;font-size:12px;font-weight:850}.fail .badge{background:#fdeaea;color:#8f1d1d}
                    dl{margin:12px 0 0}dl div{display:flex;justify-content:space-between;gap:12px;padding:7px 0;border-bottom:1px solid #eef0f2}dt{color:var(--muted)}dd{margin:0;text-align:right;font-weight:650;max-width:68%;word-break:break-word}ul{padding-left:20px}.evidence{display:flex;flex-wrap:wrap;gap:10px}.evidence a{padding:8px 11px;border:1px solid #f1b38f;border-radius:7px;color:var(--orange);font-weight:750;text-decoration:none}
                    .empty{background:#fff4d6;border:1px solid #f0d48b;border-radius:10px;padding:16px;color:#765000}.note{color:var(--muted);font-size:13px}pre{white-space:pre-wrap;word-break:break-word;background:#111827;color:#f9fafb;padding:14px;border-radius:8px;max-height:380px;overflow:auto}.kpi-grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(190px,1fr));gap:12px}.kpi{background:#fff;border:1px solid var(--border);border-radius:10px;padding:15px}.kpi strong{display:block;font-size:22px;margin-top:6px}
                    @media(max-width:700px){nav{position:static}.container{padding-left:18px;padding-right:18px}}
                  </style>
                </head>
                <body>
                  <header><div class="brand">MAPAF Enterprise Engineering Center</div><h1>{{MODULE_NAME}}</h1><div class="sub">{{SCENARIO}}</div></header>
                  <nav>
                    <a href="index.html">Command Center</a><a class="{{ACTIVE_MOBILE}}" href="mobile.html">Mobile</a><a class="{{ACTIVE_WEB}}" href="web.html">Portal</a><a class="{{ACTIVE_API}}" href="api.html">API</a><a class="{{ACTIVE_PERFORMANCE}}" href="performance.html">Performance</a><a href="../../build/allure-report/index.html" target="_blank">Allure Evidence</a>
                  </nav>
                  <main class="container">
                    <section class="hero">
                      <article class="card"><div class="label">Overall Result</div><div class="status {{STATUS_CLASS}}">{{STATUS}}</div></article>
                      <article class="card"><div class="label">Execution ID</div><div class="value">{{EXECUTION_ID}}</div></article>
                      <article class="card"><div class="label">Correlation ID</div><div class="value">{{CORRELATION_ID}}</div></article>
                      <article class="card"><div class="label">Environment</div><div class="value">{{ENVIRONMENT}}</div></article>
                      <article class="card"><div class="label">Total Duration</div><div class="value">{{DURATION}}</div></article>
                    </section>
                    <section class="section"><h2>Execution Statistics</h2>{{STATISTICS}}</section>
                    <section class="section"><h2>Business Transaction Breakdown</h2>{{TRANSACTION_BREAKDOWN}}</section>
                    <section class="section"><h2>Execution Timeline and Detailed Steps</h2>{{STEPS}}</section>
                    <section class="section"><h2>Latest Failure Showcase</h2>{{FAILURE_SHOWCASE}}</section>
                    {{QUALITY}}
                    {{DIAGNOSTICS}}
                    {{ENGINES}}
                    <section class="section"><h2>Enterprise Evidence Hub</h2>{{EVIDENCE}}<p class="note">Sensitive headers and credentials are masked by capability publishers before evidence is persisted.</p></section>
                    <section class="section"><h2>Raw Execution Data</h2><div class="card evidence"><a href="{{RAW_SUMMARY}}" target="_blank">Download enterprise summary JSON</a><a href="#" onclick="window.print();return false;">Print / Export page</a></div></section>
                  </main>
                </body>
                </html>
                """;
    }

    private String renderStatistics(String moduleKey, JsonNode root) {
        LinkedHashMap<String, String> values = new LinkedHashMap<>();
        add(values, root, "successRate", "Success Rate", "%");
        add(values, root, "steps", "Steps", "");
        add(values, root, "totalSteps", "Steps", "");
        add(values, root, "passedSteps", "Passed Steps", "");
        add(values, root, "failedSteps", "Failed Steps", "");
        add(values, root, "assertions", "Assertions", "");
        add(values, root, "screenshots", "Screenshots", "");
        add(values, root, "apiCalls", "API Calls", "");
        add(values, root, "requests", "Requests", "");
        add(values, root, "passed", "Passed Requests", "");
        add(values, root, "failed", "Failed Requests", "");
        add(values, root, "coveragePercent", "Capture Coverage", "%");
        add(values, root, "trackingConfidence", "Tracking Confidence", "");
        add(values, root, "locatorConfidence", "Locator Confidence", "");
        add(values, root, "domStability", "DOM Stability", "");
        add(values, root, "averageResponseMillis", "Average Response", " ms");
        add(values, root, "p95ResponseMillis", "P95 Response", " ms");
        add(values, root, "throughputPerSecond", "Throughput", " req/sec");
        add(values, root, "averageMs", "Average Response", " ms");
        add(values, root, "p95Ms", "P95 Response", " ms");
        add(values, root, "p99Ms", "P99 Response", " ms");
        add(values, root, "availabilityPercent", "Availability", "%");
        add(values, root, "errorRatePercent", "Error Rate", "%");
        add(values, root, "bytesReceived", "Data Received", " bytes");
        add(values, root, "bytesSent", "Data Sent", " bytes");

        if (values.isEmpty()) {
            return "<div class=\"empty\">No execution statistics were published for this module.</div>";
        }
        StringBuilder html = new StringBuilder("<div class=\"kpi-grid\">");
        values.forEach((label, value) -> html.append("<article class=\"kpi\"><div class=\"label\">")
                .append(escape(label)).append("</div><strong>").append(escape(value)).append("</strong></article>"));
        return html.append("</div>").toString();
    }

    private void add(Map<String, String> values, JsonNode root, String field, String label, String suffix) {
        JsonNode node = root.get(field);
        if (node == null || node.isNull() || !node.isValueNode()) return;
        if (values.containsKey(label)) return;
        String value;
        if (node.isFloatingPointNumber()) value = String.format("%.2f", node.asDouble());
        else value = node.asText();
        values.put(label, value + suffix);
    }

    private String renderSteps(String moduleKey, JsonNode root) {
        JsonNode steps = root.path("executionSteps");

        if (!steps.isArray() || steps.isEmpty()) {
            if ("performance".equalsIgnoreCase(moduleKey)) {
                return renderPerformanceTimeline(root);
            }

            return "<div class=\"empty\">No detailed execution timeline was published for this capability.</div>";
        }
        StringBuilder html = new StringBuilder("<div class=\"grid\">");
        for (JsonNode step : steps) {
            boolean passed = step.path("passed").asBoolean(false);
            String name = text(step, "name", "Execution step");
            int number = step.path("number").asInt(0);
            html.append("<article class=\"step ").append(passed ? "" : "fail").append("\">");
            html.append("<div class=\"step-head\"><div><div class=\"label\">Step ").append(number).append("</div><h3>").append(escape(name)).append("</h3></div><span class=\"badge\">").append(passed ? "PASS" : "FAIL").append("</span></div>");
            html.append("<dl>");
            appendField(html, step, "businessObjective", "Business Objective");
            appendField(html, step, "purpose", "Purpose");
            appendField(html, step, "workflowState", "Workflow State");
            appendField(html, step, "page", "Page");
            appendField(html, step, "action", "Action");
            appendField(html, step, "method", "Request Method");
            appendField(html, step, "url", "Request URL");
            appendField(html, step, "statusLine", "Response Status");
            appendField(html, step, "responseTimeMillis", "Response Time (ms)");
            appendField(html, step, "responseSizeBytes", "Payload Size (bytes)");
            appendField(html, step, "durationMillis", "Duration (ms)");
            html.append("</dl>");
            appendList(html, "Assertions", step.path("assertions"));
            appendList(html, "Evidence", step.path("evidence"));
            appendMap(html, "Request Headers", step.path("requestHeaders"));
            appendBody(html, "Request Body", step.path("requestBody").asText(""));
            appendMap(html, "Response Headers", step.path("responseHeaders"));
            appendBody(html, "Response Body", step.path("responseBody").asText(""));
            html.append("</article>");
        }
        return html.append("</div>").toString();
    }

    private String renderPerformanceTimeline(JsonNode root) {
        String result = normalize(firstText(
                root,
                "overallResult",
                "qualityGate",
                "result",
                "status"
        ));
        boolean passed = "PASS".equals(result);

        String profile = text(root, "profile", "Smoke");
        String engines = text(root, "engines", "k6 + JMeter");
        String requests = text(root, "requests", "0");
        String p95 = root.has("p95Ms")
                ? String.format("%.2f ms", root.path("p95Ms").asDouble())
                : "N/A";
        String recommendation = text(
                root,
                "recommendation",
                "Review the published performance diagnostics."
        );

        String statusClass = passed ? "" : "fail";
        String badge = passed ? "PASS" : "REVIEW";

        StringBuilder html = new StringBuilder("<div class=\"grid performance-pipeline\">");

        html.append("<article class=\"step ")
                .append(statusClass)
                .append("\"><div class=\"step-head\"><div><div class=\"label\">Stage 1</div>")
                .append("<h3>Initialize Workload Profile</h3></div><span class=\"badge\">")
                .append(badge)
                .append("</span></div><dl>")
                .append("<div><dt>Profile</dt><dd>")
                .append(escape(profile))
                .append("</dd></div>")
                .append("<div><dt>Execution Engines</dt><dd>")
                .append(escape(engines))
                .append("</dd></div></dl></article>");

        html.append("<article class=\"step ")
                .append(statusClass)
                .append("\"><div class=\"step-head\"><div><div class=\"label\">Stage 2</div>")
                .append("<h3>Execute k6 Workload</h3></div><span class=\"badge\">")
                .append(badge)
                .append("</span></div><dl>")
                .append("<div><dt>Engine</dt><dd>k6</dd></div>")
                .append("<div><dt>Purpose</dt><dd>HTTP workload and threshold validation</dd></div>")
                .append("</dl></article>");

        html.append("<article class=\"step ")
                .append(statusClass)
                .append("\"><div class=\"step-head\"><div><div class=\"label\">Stage 3</div>")
                .append("<h3>Execute JMeter Workload</h3></div><span class=\"badge\">")
                .append(badge)
                .append("</span></div><dl>")
                .append("<div><dt>Engine</dt><dd>Apache JMeter</dd></div>")
                .append("<div><dt>Purpose</dt><dd>Independent workload and response validation</dd></div>")
                .append("</dl></article>");

        html.append("<article class=\"step ")
                .append(statusClass)
                .append("\"><div class=\"step-head\"><div><div class=\"label\">Stage 4</div>")
                .append("<h3>Aggregate Performance Metrics</h3></div><span class=\"badge\">")
                .append(badge)
                .append("</span></div><dl>")
                .append("<div><dt>Total Requests</dt><dd>")
                .append(escape(requests))
                .append("</dd></div>")
                .append("<div><dt>P95 Response</dt><dd>")
                .append(escape(p95))
                .append("</dd></div>")
                .append("</dl></article>");

        html.append("<article class=\"step ")
                .append(statusClass)
                .append("\"><div class=\"step-head\"><div><div class=\"label\">Stage 5</div>")
                .append("<h3>Evaluate Quality Gate</h3></div><span class=\"badge\">")
                .append(badge)
                .append("</span></div><dl>")
                .append("<div><dt>Quality Gate</dt><dd>")
                .append(escape(result))
                .append("</dd></div>")
                .append("<div><dt>Recommendation</dt><dd>")
                .append(escape(recommendation))
                .append("</dd></div>")
                .append("</dl></article>");

        return html.append("</div>").toString();
    }

    private String renderQualitySection(String moduleKey, JsonNode root) {
        StringBuilder rows = new StringBuilder();
        switch (moduleKey.toLowerCase()) {
            case "mobile" -> {
                rowIfPresent(rows, root, "finalWorkflowState", "Final Workflow State");
                rowIfPresent(rows, root, "healingAttempts", "Healing Attempts");
                rowIfPresent(rows, root, "recoveredLocators", "Recovered Locators");
                rowIfPresent(rows, root, "healingConfidence", "Healing Confidence");
                rowIfPresent(rows, root, "aiRisk", "AI Risk");
                rowIfPresent(rows, root, "recommendation", "Recommendation");
            }
            case "web" -> {
                rowIfPresent(rows, root, "browser", "Browser");
                rowIfPresent(rows, root, "locatorConfidence", "Locator Confidence");
                rowIfPresent(rows, root, "domStability", "DOM Stability");
            }
            case "api" -> {
                rowIfPresent(rows, root, "medianResponseMillis", "P50 Response (ms)");
                rowIfPresent(rows, root, "p90ResponseMillis", "P90 Response (ms)");
                rowIfPresent(rows, root, "p95ResponseMillis", "P95 Response (ms)");
                rowIfPresent(rows, root, "p99ResponseMillis", "P99 Response (ms)");
                rowIfPresent(rows, root, "totalPayloadBytes", "Payload Received (bytes)");
            }
            case "performance" -> {
                rowIfPresent(rows, root, "qualityGate", "Quality Gate");
                rowIfPresent(rows, root, "bottleneck", "Diagnostic Finding");
                rowIfPresent(rows, root, "confidencePercent", "Diagnostic Confidence (%)");
                rowIfPresent(rows, root, "recommendation", "Recommendation");
                rowIfPresent(rows, root, "virtualUsers", "Virtual Users");
                rowIfPresent(rows, root, "jmeterThreads", "JMeter Threads");
            }
            default -> { }
        }
        if (rows.isEmpty()) return "";
        return "<section class=\"section\"><h2>Quality Analysis and Recommendation</h2><div class=\"card\"><dl>" + rows + "</dl></div></section>";
    }

    private void rowIfPresent(StringBuilder html, JsonNode root, String field, String label) {
        JsonNode node = root.get(field);
        if (node != null && !node.isNull()) {
            html.append("<div><dt>").append(escape(label)).append("</dt><dd>").append(escape(node.asText())).append("</dd></div>");
        }
    }

    private String renderNamedObject(String title, JsonNode node) {
        if (!node.isObject() || node.isEmpty()) return "";
        StringBuilder rows = new StringBuilder();
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String value = entry.getValue().isObject() ? entry.getValue().toPrettyString() : entry.getValue().asText();
            rows.append("<div><dt>").append(escape(label(entry.getKey()))).append("</dt><dd>").append(escape(value)).append("</dd></div>");
        }
        return "<section class=\"section\"><h2>" + escape(title) + "</h2><div class=\"card\"><dl>" + rows + "</dl></div></section>";
    }

    private String evidenceLinks(String key) {
        StringBuilder html = new StringBuilder(link("Raw enterprise summary", relativeRawSummary(key)));
        switch (key.toLowerCase()) {
            case "mobile" -> html.append(link("Mobile report folder", "../../mobile/reports/")).append(link("Allure Evidence Center", "../../build/allure-report/index.html"));
            case "web" -> html.append(link("Playwright artifacts", "../../web/artifacts/")).append(link("Allure Evidence Center", "../../build/allure-report/index.html"));
            case "api" -> html.append(link("API report folder", "../../api/reports/")).append(link("Allure Evidence Center", "../../build/allure-report/index.html"));
            case "performance" -> html.append(link("Performance dashboard", "../../performance/reports/index.html")).append(link("k6 report", "../../performance/k6/reports/smoke/index.html")).append(link("JMeter report", "../../performance/jmeter/reports/smoke/index.html"));
            default -> { }
        }
        return html.toString();
    }

    private String relativeRawSummary(String key) {
        return "../../" + key + "/reports/enterprise-summary.json";
    }

    private String link(String label, String href) {
        return "<a href=\"" + escape(href) + "\" target=\"_blank\" rel=\"noopener noreferrer\">" + escape(label) + "</a>";
    }

    private void appendField(StringBuilder html, JsonNode step, String field, String fieldLabel) {
        JsonNode value = step.get(field);
        if (value != null && !value.isNull() && !value.asText().isBlank()) {
            html.append("<div><dt>").append(escape(fieldLabel)).append("</dt><dd>").append(escape(value.asText())).append("</dd></div>");
        }
    }

    private void appendList(StringBuilder html, String title, JsonNode node) {
        if (!node.isArray() || node.isEmpty()) return;
        html.append("<h4>").append(escape(title)).append("</h4><ul>");
        for (JsonNode item : node) html.append("<li>").append(escape(item.asText())).append("</li>");
        html.append("</ul>");
    }

    private void appendMap(StringBuilder html, String title, JsonNode node) {
        if (!node.isObject() || node.isEmpty()) return;
        html.append("<h4>").append(escape(title)).append("</h4><dl>");
        node.fields().forEachRemaining(entry -> html.append("<div><dt>").append(escape(entry.getKey())).append("</dt><dd>").append(escape(entry.getValue().asText())).append("</dd></div>"));
        html.append("</dl>");
    }

    private void appendBody(StringBuilder html, String title, String body) {
        if (body == null || body.isBlank()) return;
        html.append("<h4>").append(escape(title)).append("</h4><pre>").append(escape(body)).append("</pre>");
    }

    private String formatDuration(JsonNode root) {
        if (root.has("totalDurationMillis")) return String.format("%.2f sec", root.path("totalDurationMillis").asLong() / 1000.0);
        if (root.has("durationSeconds")) return String.format("%.2f sec", root.path("durationSeconds").asDouble());
        return "N/A";
    }

    private String firstText(JsonNode node, String... fields) {
        for (String field : fields) {
            JsonNode value = node.get(field);
            if (value != null && !value.isNull() && !value.asText().isBlank()) return value.asText();
        }
        return "NOT_RUN";
    }

    private String text(JsonNode node, String field, String fallback) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? fallback : value.asText(fallback);
    }

    private String normalize(String value) {
        if (value == null) return "NOT_RUN";
        return switch (value.trim().toUpperCase()) {
            case "PASSED", "SUCCESS", "SUCCESSFUL" -> "PASS";
            case "FAILED", "FAILURE", "ERROR" -> "FAIL";
            default -> value.trim().toUpperCase();
        };
    }

    private String label(String key) {
        return key.replaceAll("([a-z])([A-Z])", "$1 $2").replace('_', ' ');
    }

    private String escape(Object value) {
        if (value == null) return "";
        return value.toString().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
    private String renderBusinessTransactions(JsonNode transactions) {
        if (!transactions.isArray() || transactions.isEmpty()) {
            return "<div class=\"empty\">No business transaction breakdown was published for this execution.</div>";
        }
        StringBuilder html = new StringBuilder("<div class=\"card\"><div style=\"overflow:auto\"><table style=\"width:100%;border-collapse:collapse\"><thead><tr><th style=\"text-align:left;padding:10px\">Transaction</th><th>Requests</th><th>P95</th><th>Error Rate</th><th>TTFB</th><th>Connection</th></tr></thead><tbody>");
        for (JsonNode tx : transactions) {
            html.append("<tr style=\"border-top:1px solid #eef0f2\"><td style=\"padding:10px;font-weight:700\">").append(escape(tx.path("name").asText())).append("</td>")
                .append("<td style=\"text-align:center\">").append(tx.path("requests").asInt()).append("</td>")
                .append("<td style=\"text-align:center\">").append(String.format("%.2f ms", tx.path("p95Ms").asDouble())).append("</td>")
                .append("<td style=\"text-align:center\">").append(String.format("%.3f%%", tx.path("errorRatePercent").asDouble())).append("</td>")
                .append("<td style=\"text-align:center\">").append(String.format("%.2f ms", tx.path("waitingTtfbMs").asDouble())).append("</td>")
                .append("<td style=\"text-align:center\">").append(String.format("%.2f ms", tx.path("connectionTimeMs").asDouble())).append("</td></tr>");
        }
        return html.append("</tbody></table></div></div>").toString();
    }

    private String renderFailureShowcase(String moduleKey) {
        Path path = Path.of(moduleKey, "reports", "failure-showcase.json");
        if (!Files.isRegularFile(path)) {
            return "<div class=\"empty\">No deterministic failure showcase has been executed. Run the module failure demo to publish debugging evidence.</div>";
        }
        try {
            JsonNode failure = MAPPER.readTree(path.toFile());
            StringBuilder html = new StringBuilder("<article class=\"step fail\"><div class=\"step-head\"><div><div class=\"label\">Expected Resilience Scenario</div><h3>")
                    .append(escape(failure.path("scenario").asText("Failure showcase")))
                    .append("</h3></div><span class=\"badge\">VALIDATED</span></div><dl>");
            String[][] fields = {{"showcaseStatus","Showcase Status"},{"failureCategory","Failure Category"},{"failedTransaction","Failed Transaction"},{"expected","Expected"},{"actual","Actual"},{"correlationId","Correlation ID"},{"retryable","Retryable"},{"diagnosis","AI-ready Root Cause"},{"confidence","Confidence"},{"recommendation","Recommended Action"}};
            for (String[] field : fields) {
                JsonNode value = failure.get(field[0]);
                if (value != null && !value.isNull()) {
                    html.append("<div><dt>").append(escape(field[1])).append("</dt><dd>").append(escape(value.asText())).append("</dd></div>");
                }
            }
            html.append("</dl><div class=\"evidence\"><a href=\"../../").append(escape(path.toString())).append("\" target=\"_blank\">Open failure JSON</a><a href=\"../../build/allure-report/index.html\" target=\"_blank\">Open Allure failure evidence</a></div></article>");
            return html.toString();
        } catch (Exception exception) {
            return "<div class=\"empty\">Failure showcase could not be read: " + escape(exception.getMessage()) + "</div>";
        }
    }

}
