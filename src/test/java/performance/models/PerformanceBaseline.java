package performance.models;

public class PerformanceBaseline {

    private final double p95ResponseTime;
    private final double errorRate;
    private final double throughput;

    public PerformanceBaseline(
            double p95ResponseTime,
            double errorRate,
            double throughput
    ) {
        this.p95ResponseTime = p95ResponseTime;
        this.errorRate = errorRate;
        this.throughput = throughput;
    }

    public double getP95ResponseTime() {
        return p95ResponseTime;
    }

    public double getErrorRate() {
        return errorRate;
    }

    public double getThroughput() {
        return throughput;
    }
}
