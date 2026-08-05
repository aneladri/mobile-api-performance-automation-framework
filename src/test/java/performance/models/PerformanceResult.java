package performance.models;

import java.time.Instant;

public class PerformanceResult {

    private final String tool;
    private final String scenario;
    private final double averageResponseTime;
    private final double p95ResponseTime;
    private final double p99ResponseTime;
    private final double errorRate;
    private final double throughput;
    private final long sampleCount;
    private final Instant executionTime;

    public PerformanceResult(
            String tool,
            String scenario,
            double averageResponseTime,
            double p95ResponseTime,
            double p99ResponseTime,
            double errorRate,
            double throughput,
            long sampleCount,
            Instant executionTime
    ) {
        this.tool = tool;
        this.scenario = scenario;
        this.averageResponseTime = averageResponseTime;
        this.p95ResponseTime = p95ResponseTime;
        this.p99ResponseTime = p99ResponseTime;
        this.errorRate = errorRate;
        this.throughput = throughput;
        this.sampleCount = sampleCount;
        this.executionTime = executionTime;
    }

    public String getTool() {
        return tool;
    }

    public String getScenario() {
        return scenario;
    }

    public double getAverageResponseTime() {
        return averageResponseTime;
    }

    public double getP95ResponseTime() {
        return p95ResponseTime;
    }

    public double getP99ResponseTime() {
        return p99ResponseTime;
    }

    public double getErrorRate() {
        return errorRate;
    }

    public double getThroughput() {
        return throughput;
    }

    public long getSampleCount() {
        return sampleCount;
    }

    public Instant getExecutionTime() {
        return executionTime;
    }
}
