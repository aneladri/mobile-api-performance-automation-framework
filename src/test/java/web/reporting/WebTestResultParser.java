package web.reporting;

public class WebTestResultParser {

    private int total;
    private int passed;
    private int failed;
    private int skipped;
    private double durationSeconds;
    private int fileCount;

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

    public double getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(
            double durationSeconds
    ) {
        this.durationSeconds = durationSeconds;
    }

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }
}
