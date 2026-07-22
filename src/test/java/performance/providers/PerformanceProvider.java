package performance.providers;

import performance.models.PerformanceResult;

public interface PerformanceProvider {

    PerformanceResult execute(
            String testPlan
    );
}
