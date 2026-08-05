package api.enterprise.logging;

import api.enterprise.metrics.ApiExecutionSummary;
import api.enterprise.model.ApiExecutionStep;
import api.enterprise.model.CorrelationContext;

import java.util.Map;

public final class ApiExecutionLogger {

    private static final String LINE = "==============================================================";
    private static final String DIVIDER = "--------------------------------------------------------------";

    private ApiExecutionLogger() {
    }

    public static void banner(
            String scenario,
            String environment,
            String baseUrl,
            CorrelationContext context) {
        System.out.println();
        System.out.println(LINE);
        System.out.println("            MAPAF API AUTOMATION PLATFORM");
        System.out.println(LINE);
        field("Execution ID", context.executionId());
        field("Correlation ID", context.correlationId());
        field("Scenario", scenario);
        field("Environment", environment);
        field("Technology", "REST Assured");
        field("Framework", "MAPAF Enterprise");
        field("Endpoint", baseUrl);
        System.out.println(LINE);
    }

    public static void step(ApiExecutionStep step) {
        System.out.println();
        System.out.println(DIVIDER);
        System.out.printf("STEP %d : %s%n", step.number(), step.name());
        System.out.println(DIVIDER);
        section("Purpose");
        System.out.println(step.purpose());

        section("Request");
        System.out.printf("%s %s%n", step.method(), step.url());
        printHeaders(step.requestHeaders());
        printBody("Request Body", step.requestBody());

        section("Response");
        field("Status", step.statusCode() + " " + safeStatus(step.statusLine()));
        field("Response Time", step.responseTimeMillis() + " ms");
        field("Payload Size", step.responseSizeBytes() + " bytes");
        printHeaders(step.responseHeaders());
        printBody("Response Body", step.responseBody());

        section("Assertions");
        step.assertions().forEach(assertion -> System.out.println("✓ " + assertion));

        section("Result");
        System.out.println(step.passed() ? "PASSED" : "FAILED");
    }

    public static void summary(ApiExecutionSummary summary) {
        System.out.println();
        System.out.println(LINE);
        System.out.println("Execution Summary");
        System.out.println(DIVIDER);
        field("API Calls", summary.apiCalls());
        field("Assertions", summary.assertions());
        field("Passed Steps", summary.passedSteps());
        field("Failed Steps", summary.failedSteps());
        field("Success Rate", String.format("%.2f%%", summary.successRate()));
        field("Average Response", String.format("%.2f ms", summary.averageResponseMillis()));
        field("Median / P50", summary.medianResponseMillis() + " ms");
        field("P90", summary.p90ResponseMillis() + " ms");
        field("P95", summary.p95ResponseMillis() + " ms");
        field("P99", summary.p99ResponseMillis() + " ms");
        field("Fastest", summary.fastestResponseMillis() + " ms");
        field("Slowest", summary.slowestResponseMillis() + " ms");
        field("Payload Received", summary.totalPayloadBytes() + " bytes");
        field("Total Duration", String.format("%.2f sec", summary.totalDurationMillis() / 1000.0));
        field("Overall Result", summary.result());
        System.out.println(LINE);
    }

    private static void printHeaders(Map<String, String> headers) {
        if (headers == null || headers.isEmpty()) {
            return;
        }
        System.out.println();
        System.out.println("Headers");
        headers.forEach((key, value) ->
                System.out.printf("%-20s : %s%n", key, mask(key, value)));
    }

    private static String mask(String key, String value) {
        String normalized = key.toLowerCase();
        if (normalized.contains("authorization")
                || normalized.contains("api-key")
                || normalized.contains("token")) {
            return "********";
        }
        return value;
    }

    private static void printBody(String title, String body) {
        if (body == null || body.isBlank()) {
            return;
        }
        System.out.println();
        System.out.println(title);
        System.out.println(body);
    }

    private static void section(String title) {
        System.out.println();
        System.out.println(title);
    }

    private static String safeStatus(String statusLine) {
        if (statusLine == null) {
            return "";
        }
        return statusLine.replaceFirst("^HTTP/\\S+\\s+\\d+\\s*", "").trim();
    }

    private static void field(String name, Object value) {
        System.out.printf("%-20s : %s%n", name, value);
    }
}
