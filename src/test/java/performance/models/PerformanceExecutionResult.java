package performance.models;

import java.nio.file.Path;
import java.time.Instant;

public class PerformanceExecutionResult {

    private final String tool;
    private final String testPlan;
    private final Path resultFile;
    private final Path dashboardDirectory;
    private final Instant executionTime;
    private final PerformanceResult performanceResult;

    public PerformanceExecutionResult(
            String tool,
            String testPlan,
            Path resultFile,
            Path dashboardDirectory,
            Instant executionTime,
            PerformanceResult performanceResult
    ) {
        this.tool = tool;
        this.testPlan = testPlan;
        this.resultFile = resultFile;
        this.dashboardDirectory = dashboardDirectory;
        this.executionTime = executionTime;
        this.performanceResult = performanceResult;
    }

    public String getTool() {
        return tool;
    }

    public String getTestPlan() {
        return testPlan;
    }

    public Path getResultFile() {
        return resultFile;
    }

    public Path getDashboardDirectory() {
        return dashboardDirectory;
    }

    public Instant getExecutionTime() {
        return executionTime;
    }

    public PerformanceResult getPerformanceResult() {
        return performanceResult;
    }

    public boolean hasDashboard() {
        return dashboardDirectory != null;
    }

    public boolean hasPerformanceResult() {
        return performanceResult != null;
    }
}
