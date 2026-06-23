package core.ai;

public class PerformanceBaseline {

    private final double p95Ms;
    private final double errorRate;
    private final double throughput;

    public PerformanceBaseline(
            double p95Ms,
            double errorRate,
            double throughput
    ) {
        this.p95Ms = p95Ms;
        this.errorRate = errorRate;
        this.throughput = throughput;
    }

    public double getP95Ms() {
        return p95Ms;
    }

    public double getErrorRate() {
        return errorRate;
    }

    public double getThroughput() {
        return throughput;
    }
}
