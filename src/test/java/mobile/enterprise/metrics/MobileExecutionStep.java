package mobile.enterprise.metrics;

import java.util.List;

public record MobileExecutionStep(
        int number,
        String name,
        String businessObjective,
        String workflowState,
        long durationMillis,
        List<String> assertions,
        List<String> evidence,
        boolean passed
) {
}
