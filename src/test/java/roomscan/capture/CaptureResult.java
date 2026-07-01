package roomscan.capture;

public class CaptureResult {

    private final String scanId;
    private final String fixtureName;
    private final String scanFilePath;

    public CaptureResult(
            String scanId,
            String fixtureName,
            String scanFilePath
    ) {
        this.scanId = scanId;
        this.fixtureName = fixtureName;
        this.scanFilePath = scanFilePath;
    }

    public String getScanId() {
        return scanId;
    }

    public String getFixtureName() {
        return fixtureName;
    }

    public String getScanFilePath() {
        return scanFilePath;
    }
}
