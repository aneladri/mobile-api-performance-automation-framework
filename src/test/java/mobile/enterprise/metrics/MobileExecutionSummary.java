package mobile.enterprise.metrics;

import core.enterprise.reporting.BusinessTransaction;
import mobile.enterprise.diagnostics.DeviceDiagnostics;

import java.util.List;

public record MobileExecutionSummary(
        String executionId,
        String correlationId,
        String traceId,
        String scenario,
        String environment,
        int steps,
        int passedSteps,
        int failedSteps,
        int assertions,
        int screenshots,
        double successRate,
        long totalDurationMillis,
        String finalWorkflowState,
        int coveragePercent,
        String trackingConfidence,
        int healingAttempts,
        int recoveredLocators,
        int healingConfidence,
        DeviceDiagnostics diagnostics,
        String aiRisk,
        String recommendation,
        String result,
        List<MobileExecutionStep> executionSteps,
        List<BusinessTransaction> businessTransactions
) {
}
