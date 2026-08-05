package web.enterprise.logging;

import core.enterprise.execution.EnterpriseExecutionContext;
import web.enterprise.metrics.WebExecutionStep;
import web.enterprise.metrics.WebExecutionSummary;

public final class WebExecutionLogger {

    private static final String LINE = "=".repeat(70);
    private static final String DIVIDER = "-".repeat(70);

    private WebExecutionLogger() {
    }

    public static void banner(
            EnterpriseExecutionContext context,
            String environment,
            String browser,
            boolean headless) {
        System.out.println();
        System.out.println(LINE);
        System.out.println("        MAPAF WEB AUTOMATION PLATFORM");
        System.out.println(LINE);
        field("Execution ID", context.getExecutionId());
        field("Correlation ID", context.getCorrelationId());
        field("Trace ID", context.getTraceId());
        field("Scenario", context.getScenario());
        field("Environment", environment);
        field("Browser", browser);
        field("Technology", "Playwright");
        field("Execution Mode", headless ? "Headless" : "Headed");
        field("Trace / Video", "Enabled / Enabled");
        field("Accessibility", "Enabled");
        System.out.println(LINE);
    }

    public static void step(WebExecutionStep step) {
        System.out.println();
        System.out.println(DIVIDER);
        System.out.printf("STEP %d : %s%n", step.number(), step.name());
        System.out.println(DIVIDER);
        field("Business Objective", step.businessObjective());
        field("Page", step.page());
        field("Action", step.action());
        field("Duration", step.durationMillis() + " ms");
        System.out.println();
        System.out.println("Assertions");
        String assertionSymbol = step.passed() ? "✓" : "✗";
        step.assertions().forEach(value -> System.out.println(assertionSymbol + " " + value));
        System.out.println();
        System.out.println("Evidence");
        step.evidence().forEach(value -> System.out.println("✓ " + value));
        System.out.println();
        field("Result", step.passed() ? "PASSED" : "FAILED");
    }

    public static void summary(WebExecutionSummary summary) {
        System.out.println();
        System.out.println(LINE);
        System.out.println("Execution Summary");
        System.out.println(DIVIDER);
        field("Workflow", "RoomScan Portal Review");
        field("Steps", summary.totalSteps());
        field("Passed / Failed", summary.passedSteps() + " / " + summary.failedSteps());
        field("Assertions", summary.assertions());
        field("Screenshots", summary.screenshots());
        field("Success Rate", String.format("%.2f%%", summary.successRate()));
        field("Console Entries", summary.diagnostics().consoleEntries());
        field("Page Errors", summary.diagnostics().pageErrors());
        field("Network Failures", summary.diagnostics().networkFailures());
        field("Accessibility", summary.diagnostics().accessibilityStatus());
        field("Locator Confidence", summary.locatorConfidence());
        field("DOM Stability", summary.domStability());
        field("Trace", summary.diagnostics().traceStatus());
        field("Video", summary.diagnostics().videoStatus());
        field("Total Duration", String.format("%.2f sec", summary.totalDurationMillis() / 1000.0));
        field("Business Outcome", "Floor plan reviewed, approved and submitted");
        field("Overall Result", summary.result());
        System.out.println(LINE);
    }

    private static void field(String name, Object value) {
        System.out.printf("%-28s : %s%n", name, value);
    }
}
