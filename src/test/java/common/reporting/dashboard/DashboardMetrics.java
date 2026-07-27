package common.reporting.dashboard;

public class DashboardMetrics {

    private int totalTests;
    private int passed;
    private int failed;
    private int skipped;
    private double passRate;
    private long durationSeconds;

    public DashboardMetrics() {
    }

    public DashboardMetrics(
            int totalTests,
            int passed,
            int failed,
            int skipped,
            long durationSeconds
    ) {
        this.totalTests = totalTests;
        this.passed = passed;
        this.failed = failed;
        this.skipped = skipped;
        this.durationSeconds = durationSeconds;
        recalculatePassRate();
    }

    public void recalculatePassRate() {
        this.passRate = totalTests == 0
                ? 0.0
                : passed * 100.0 / totalTests;
    }

    public void add(
            int total,
            int passed,
            int failed,
            int skipped,
            long durationSeconds
    ) {
        this.totalTests += total;
        this.passed += passed;
        this.failed += failed;
        this.skipped += skipped;
        this.durationSeconds += durationSeconds;

        recalculatePassRate();
    }

    public int getTotalTests() {
        return totalTests;
    }

    public void setTotalTests(int totalTests) {
        this.totalTests = totalTests;
        recalculatePassRate();
    }

    public int getPassed() {
        return passed;
    }

    public void setPassed(int passed) {
        this.passed = passed;
        recalculatePassRate();
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
