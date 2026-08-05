package web.enterprise.metrics;

import java.util.List;

public record WebExecutionStep(
        int number,
        String name,
        String businessObjective,
        String page,
        String action,
        long durationMillis,
        List<String> assertions,
        List<String> evidence,
        boolean passed
) {
}
