package core.enterprise.reporting;

import api.enterprise.metrics.ApiExecutionSummary;
import common.reporting.model.ExecutionEnvironment;
import common.reporting.model.ExecutionMetrics;
import common.reporting.model.ExecutionStatus;
import common.reporting.model.ExecutionSummary;
import performance.enterprise.EnterprisePerformanceMetrics;

public final class EnterpriseSummaryAdapter {
    private EnterpriseSummaryAdapter() {}

    public static ExecutionSummary fromApi(ApiExecutionSummary source) {
        ExecutionSummary summary = new ExecutionSummary(
                "API",
                "PASSED".equalsIgnoreCase(source.result()) ? ExecutionStatus.PASS : ExecutionStatus.FAIL,
                ExecutionMetrics.fromCounts(source.passedSteps() + source.failedSteps(),
                        source.passedSteps(), source.failedSteps(), 0,
                        Math.round(source.totalDurationMillis() / 1000.0)),
                environment(source.environment(), source.executionId())
        );
        summary.setContractVersion("2.0");
        summary.addDetail("correlationId", source.correlationId());
        summary.addDetail("scenario", source.scenario());
        summary.addDetail("apiCalls", source.apiCalls());
        summary.addDetail("assertions", source.assertions());
        summary.addDetail("averageResponseMillis", source.averageResponseMillis());
        summary.addDetail("p95ResponseMillis", source.p95ResponseMillis());
        summary.addDetail("payloadBytes", source.totalPayloadBytes());
        return summary;
    }

    public static ExecutionSummary fromPerformance(EnterprisePerformanceMetrics source) {
        ExecutionSummary summary = new ExecutionSummary(
                "Performance",
                "PASS".equalsIgnoreCase(source.qualityGate) ? ExecutionStatus.PASS : ExecutionStatus.FAIL,
                ExecutionMetrics.fromCounts(source.requests, source.passed, source.failed, 0, source.durationSeconds),
                environment(source.environment, source.executionId)
        );
        summary.setContractVersion("2.0");
        summary.addDetail("scenario", source.scenario);
        summary.addDetail("throughputPerSecond", source.throughputPerSecond);
        summary.addDetail("averageMillis", source.averageMs);
        summary.addDetail("p95Millis", source.p95Ms);
        summary.addDetail("p99Millis", source.p99Ms);
        summary.addDetail("errorRatePercent", source.errorRatePercent);
        summary.addDetail("bytesReceived", source.bytesReceived);
        summary.addDetail("bytesSent", source.bytesSent);
        summary.addDetail("qualityGate", source.qualityGate);
        summary.addDetail("diagnosticFinding", source.bottleneck);
        return summary;
    }

    private static ExecutionEnvironment environment(String environment, String executionId) {
        return new ExecutionEnvironment(
                environment,
                System.getProperty("mapaf.build", "local"),
                System.getProperty("mapaf.branch", "local"),
                System.getProperty("mapaf.commit", "local"),
                executionId
        );
    }
}
