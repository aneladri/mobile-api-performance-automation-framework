package mobile.enterprise.logging;

import core.enterprise.execution.EnterpriseExecutionContext;
import mobile.enterprise.metrics.MobileExecutionStep;
import mobile.enterprise.metrics.MobileExecutionSummary;

public final class MobileExecutionLogger {

    private static final String LINE = "=".repeat(70);
    private static final String DIVIDER = "-".repeat(70);

    private MobileExecutionLogger() {
    }

    public static void banner(EnterpriseExecutionContext context, String environment, String captureProvider) {
        System.out.println();
        System.out.println(LINE);
        System.out.println("        MAPAF MOBILE ENTERPRISE PLATFORM");
        System.out.println(LINE);
        field("Execution ID", context.getExecutionId());
        field("Correlation ID", context.getCorrelationId());
        field("Trace ID", context.getTraceId());
        field("Scenario", context.getScenario());
        field("Environment", environment);
        field("Automation", "Appium");
        field("AI Healing", "Enabled");
        field("Capture Provider", captureProvider);
        System.out.println(LINE);
    }

    public static void step(MobileExecutionStep step) {
        System.out.println();
        System.out.println(DIVIDER);
        System.out.printf("STEP %d : %s%n", step.number(), step.name());
        System.out.println(DIVIDER);
        field("Business Objective", step.businessObjective());
        field("Workflow State", step.workflowState());
        field("Duration", String.format("%.2f sec", step.durationMillis() / 1000.0));
        System.out.println();
        System.out.println("Assertions");
        String symbol = step.passed() ? "✓" : "✗";
        step.assertions().forEach(assertion -> System.out.println(symbol + " " + assertion));
        System.out.println();
        System.out.println("Evidence");
        step.evidence().forEach(evidence -> System.out.println("✓ " + evidence));
        System.out.println();
        field("Result", step.passed() ? "PASSED" : "FAILED");
    }

    public static void summary(MobileExecutionSummary summary) {
        System.out.println();
        System.out.println(LINE);
        System.out.println("Execution Summary");
        System.out.println(DIVIDER);
        field("Workflow", "RoomScan Mobile End-to-End");
        field("Steps", summary.steps());
        field("Passed / Failed", summary.passedSteps() + " / " + summary.failedSteps());
        field("Assertions", summary.assertions());
        field("Screenshots", summary.screenshots());
        field("Success Rate", String.format("%.2f%%", summary.successRate()));
        field("Final Workflow State", summary.finalWorkflowState());
        field("Capture Coverage", summary.coveragePercent() + "%");
        field("Tracking Confidence", summary.trackingConfidence());
        field("Healing Attempts", summary.healingAttempts());
        field("Recovered Locators", summary.recoveredLocators());
        field("Healing Confidence", summary.healingConfidence() + "%");
        field("Device", summary.diagnostics().deviceName());
        field("Platform", summary.diagnostics().platform() + " " + summary.diagnostics().platformVersion());
        field("Session ID", summary.diagnostics().sessionId());
        field("AI Risk", summary.aiRisk());
        field("Recommendation", summary.recommendation());
        field("Total Duration", String.format("%.2f sec", summary.totalDurationMillis() / 1000.0));
        field("Overall Result", summary.result());
        System.out.println(LINE);
    }

    private static void field(String name, Object value) {
        System.out.printf("%-28s : %s%n", name, value);
    }
}
