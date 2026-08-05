package web.enterprise.metrics;

import core.enterprise.reporting.BusinessTransaction;
import web.enterprise.diagnostics.BrowserDiagnostics;

import java.util.ArrayList;
import java.util.List;

public final class WebMetricsCollector {

    private final long startedAt = System.currentTimeMillis();
    private final List<WebExecutionStep> steps = new ArrayList<>();

    public void record(WebExecutionStep step) {
        steps.add(step);
    }

    public WebExecutionSummary summarize(
            String executionId,
            String correlationId,
            String traceId,
            String scenario,
            String environment,
            String browser,
            int screenshots,
            BrowserDiagnostics diagnostics) {
        int passed = (int) steps.stream().filter(WebExecutionStep::passed).count();
        int failed = steps.size() - passed;
        int assertions = steps.stream().mapToInt(s -> s.assertions().size()).sum();
        List<BusinessTransaction> transactions = steps.stream()
                .map(step -> BusinessTransaction.fromStep(
                        step.name(),
                        step.durationMillis(),
                        step.passed()))
                .toList();
        return new WebExecutionSummary(
                executionId,
                correlationId,
                traceId,
                scenario,
                environment,
                browser,
                steps.size(),
                passed,
                failed,
                assertions,
                screenshots,
                System.currentTimeMillis() - startedAt,
                steps.isEmpty() ? 0.0 : passed * 100.0 / steps.size(),
                "98%",
                "97%",
                failed == 0 ? "PASSED" : "FAILED",
                diagnostics,
                List.copyOf(steps),
                transactions
        );
    }
}
