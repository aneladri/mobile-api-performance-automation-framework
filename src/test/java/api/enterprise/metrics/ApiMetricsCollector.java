package api.enterprise.metrics;

import api.enterprise.model.ApiExecutionStep;
import core.enterprise.reporting.BusinessTransaction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class ApiMetricsCollector {

    private final long startedAt = System.currentTimeMillis();
    private final List<ApiExecutionStep> steps = new ArrayList<>();

    public void record(ApiExecutionStep step) {
        steps.add(step);
    }

    public ApiExecutionSummary summarize(
            String executionId,
            String correlationId,
            String scenario,
            String environment) {

        List<Long> responseTimes = steps.stream()
                .filter(ApiExecutionStep::networkCall)
                .map(ApiExecutionStep::responseTimeMillis)
                .sorted(Comparator.naturalOrder())
                .toList();

        int assertions = steps.stream()
                .mapToInt(step -> step.assertions().size())
                .sum();
        int passed = (int) steps.stream().filter(ApiExecutionStep::passed).count();
        int failed = steps.size() - passed;
        double average = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        long payloadBytes = steps.stream()
                .filter(ApiExecutionStep::networkCall)
                .mapToLong(ApiExecutionStep::responseSizeBytes)
                .sum();
        String result = failed == 0 ? "PASSED" : "FAILED";
        List<BusinessTransaction> transactions = steps.stream()
                .map(step -> new BusinessTransaction(
                        step.name(),
                        step.networkCall() ? 1 : 0,
                        step.passed() ? 1 : 0,
                        step.passed() ? 0 : 1,
                        step.passed() ? 0.0 : 100.0,
                        step.responseTimeMillis(),
                        step.responseTimeMillis(),
                        0.0,
                        0.0,
                        step.passed() ? "PASS" : "FAIL"))
                .toList();

        return new ApiExecutionSummary(
                executionId,
                correlationId,
                scenario,
                environment,
                (int) steps.stream().filter(ApiExecutionStep::networkCall).count(),
                assertions,
                passed,
                failed,
                steps.isEmpty() ? 0.0 : (passed * 100.0 / steps.size()),
                average,
                percentile(responseTimes, 50),
                percentile(responseTimes, 90),
                percentile(responseTimes, 95),
                percentile(responseTimes, 99),
                responseTimes.isEmpty() ? 0 : responseTimes.getFirst(),
                responseTimes.isEmpty() ? 0 : responseTimes.getLast(),
                payloadBytes,
                System.currentTimeMillis() - startedAt,
                result,
                List.copyOf(steps),
                transactions
        );
    }

    private long percentile(List<Long> sorted, int percentile) {
        if (sorted.isEmpty()) {
            return 0;
        }
        int index = (int) Math.ceil(percentile / 100.0 * sorted.size()) - 1;
        return sorted.get(Math.max(0, Math.min(index, sorted.size() - 1)));
    }
}
