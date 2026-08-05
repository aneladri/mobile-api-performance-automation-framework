package api.enterprise.metrics;

import api.enterprise.model.ApiExecutionStep;
import core.enterprise.reporting.BusinessTransaction;

import java.util.List;

public record ApiExecutionSummary(
        String executionId,
        String correlationId,
        String scenario,
        String environment,
        int apiCalls,
        int assertions,
        int passedSteps,
        int failedSteps,
        double successRate,
        double averageResponseMillis,
        long medianResponseMillis,
        long p90ResponseMillis,
        long p95ResponseMillis,
        long p99ResponseMillis,
        long fastestResponseMillis,
        long slowestResponseMillis,
        long totalPayloadBytes,
        long totalDurationMillis,
        String result,
        List<ApiExecutionStep> executionSteps,
        List<BusinessTransaction> businessTransactions
) {
}
