package common.reporting.model;

public class ExecutionMetrics {

    private int total;
    private int passed;
    private int failed;
    private int skipped;
    private double passRate;
    private long durationSeconds;

    public ExecutionMetrics() {
    }

    public ExecutionMetrics(
            int total,
            int passed,
            int failed,
            int skipped,
            double passRate,
            long durationSeconds
    ) {
        this.total = total;
        this.passed = passed;
        this.failed = failed;
        this.skipped = skipped;
        this.passRate = passRate;
        this.durationSeconds = durationSeconds;
    }

    public static ExecutionMetrics fromCounts(
            int total,
            int passed,
            int failed,
            int skipped,
            long durationSeconds
    ) {
        double calculatedPassRate =
                total == 0 ? 0.0 : (passed * 100.0) / total;

        return new ExecutionMetrics(
                total,
                passed,
                failed,
                skipped,
                calculatedPassRate,
                durationSeconds
        );
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPassed() {
        return passed;
    }

    public void setPassed(int passed) {
        this.passed = passed;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public int getSkipped() {
        return skipped;
    }

    public void setSkipped(int skipped) {
        this.skipped = skipped;
    }

    public double getPassRate() {
        return passRate;
    }

    public void setPassRate(double passRate) {
        this.passRate = passRate;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
}
