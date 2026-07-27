package performance.reporting;

public class PerformanceParseResult {

    private int total;
    private int passed;
    private int failed;
    private long durationSeconds;
    private int requestCount;
    private int fileCount;

    public void add(
            int total,
            int passed,
            int failed,
            long durationSeconds,
            int requestCount
    ) {
        this.total += total;
        this.passed += passed;
        this.failed += failed;
        this.durationSeconds += durationSeconds;
        this.requestCount += requestCount;
        this.fileCount++;
    }

    public void merge(PerformanceParseResult other) {
        if (other == null) {
            return;
        }

        this.total += other.total;
        this.passed += other.passed;
        this.failed += other.failed;
        this.durationSeconds += other.durationSeconds;
        this.requestCount += other.requestCount;
        this.fileCount += other.fileCount;
    }

    public int getTotal() {
        return total;
    }

    public int getPassed() {
        return passed;
    }

    public int getFailed() {
        return failed;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public int getFileCount() {
        return fileCount;
    }
}
