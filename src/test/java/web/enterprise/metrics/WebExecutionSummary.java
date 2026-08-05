package web.enterprise.metrics;

import core.enterprise.reporting.BusinessTransaction;
import web.enterprise.diagnostics.BrowserDiagnostics;

import java.util.List;

public record WebExecutionSummary(
        String executionId,
        String correlationId,
        String traceId,
        String scenario,
        String environment,
        String browser,
        int totalSteps,
        int passedSteps,
        int failedSteps,
        int assertions,
        int screenshots,
        long totalDurationMillis,
        double successRate,
        String locatorConfidence,
        String domStability,
        String result,
        BrowserDiagnostics diagnostics,
        List<WebExecutionStep> executionSteps,
        List<BusinessTransaction> businessTransactions
) {
}
