package roomscan.capture;

import java.util.List;

public class CaptureQualityReport {

    private final int coveragePercent;
    private final String trackingConfidence;
    private final List<String> warnings;

    public CaptureQualityReport(
            int coveragePercent,
            String trackingConfidence,
            List<String> warnings
    ) {
        this.coveragePercent = coveragePercent;
        this.trackingConfidence = trackingConfidence;
        this.warnings = warnings;
    }

    public int getCoveragePercent() {
        return coveragePercent;
    }

    public String getTrackingConfidence() {
        return trackingConfidence;
    }

    public List<String> getWarnings() {
        return warnings;
    }
}
