package api.reporting;

public class ApiTestResultParser {

    private int total;
    private int passed;
    private int failed;
    private int skipped;
    private long durationMilliseconds;
    private int fileCount;

    public void add(
            int total,
            int passed,
            int failed,
            int skipped,
            long durationMilliseconds
    ) {
        this.total += total;
        this.passed += passed;
        this.failed += failed;
        this.skipped += skipped;
        this.durationMilliseconds += durationMilliseconds;
        this.fileCount++;
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

    public int getSkipped() {
        return skipped;
    }

    public long getDurationSeconds() {
        if (durationMilliseconds <= 0) {
            return 0;
        }

        return Math.max(
                1,
                Math.round(durationMilliseconds / 1000.0)
        );
    }

    public int getFileCount() {
        return fileCount;
    }
}
