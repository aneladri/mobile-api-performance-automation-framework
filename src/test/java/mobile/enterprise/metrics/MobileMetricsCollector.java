package mobile.enterprise.metrics;

import core.ai.healing.HealingMetrics;
import core.enterprise.reporting.BusinessTransaction;
import mobile.enterprise.diagnostics.DeviceDiagnostics;
import roomscan.capture.CaptureQualityReport;

import java.util.ArrayList;
import java.util.List;

public final class MobileMetricsCollector {

    private final long startedAt = System.currentTimeMillis();
    private final List<MobileExecutionStep> steps = new ArrayList<>();

    public void record(MobileExecutionStep step) {
        steps.add(step);
    }

    public MobileExecutionSummary summarize(
            String executionId,
            String correlationId,
            String traceId,
            String scenario,
            String environment,
            int screenshots,
            String finalWorkflowState,
            CaptureQualityReport quality,
            HealingMetrics healing,
            DeviceDiagnostics diagnostics) {

        int passed = (int) steps.stream().filter(MobileExecutionStep::passed).count();
        int failed = steps.size() - passed;
        int assertions = steps.stream().mapToInt(step -> step.assertions().size()).sum();
        int recovered = healing.getCacheHits() + healing.getRuleHits() + healing.getClaudeHits();
        String result = failed == 0 ? "PASSED" : "FAILED";
        String risk = failed == 0 && healing.getBudgetBlocks() == 0 ? "LOW" : "MEDIUM";
        List<BusinessTransaction> transactions = steps.stream()
                .map(step -> BusinessTransaction.fromStep(
                        step.name(),
                        step.durationMillis(),
                        step.passed()))
                .toList();

        return new MobileExecutionSummary(
                executionId,
                correlationId,
                traceId,
                scenario,
                environment,
                steps.size(),
                passed,
                failed,
                assertions,
                screenshots,
                steps.isEmpty() ? 0.0 : passed * 100.0 / steps.size(),
                System.currentTimeMillis() - startedAt,
                finalWorkflowState,
                quality.getCoveragePercent(),
                quality.getTrackingConfidence(),
                healing.getHealingAttempts(),
                recovered,
                healing.getSuccessfulCandidateConfidence(),
                diagnostics,
                risk,
                failed == 0 ? "READY FOR WEB REVIEW" : "INVESTIGATE MOBILE WORKFLOW",
                result,
                List.copyOf(steps),
                transactions
        );
    }
}
