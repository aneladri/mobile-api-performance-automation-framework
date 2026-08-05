package roomscan.capture;

import java.util.Collections;
import java.util.UUID;

public class MockCaptureProvider implements CaptureProvider {

    private boolean captureStarted;
    private String fixtureName;

    @Override
    public void startCapture(String fixtureName) {
        this.fixtureName = fixtureName;
        this.captureStarted = true;
    }

    @Override
    public boolean isCaptureComplete() {
        return captureStarted;
    }

    @Override
    public CaptureResult finishCapture() {
        if (!captureStarted) {
            throw new IllegalStateException(
                    "Cannot finish capture before capture is started"
            );
        }

        captureStarted = false;

        return new CaptureResult(
                UUID.randomUUID().toString(),
                fixtureName,
                "src/test/resources/roomscan/fixtures/" + fixtureName
        );
    }

    @Override
    public void cancelCapture() {
        captureStarted = false;
    }

    @Override
    public CaptureQualityReport getQualityReport() {
        return new CaptureQualityReport(
                100,
                "HIGH",
                Collections.emptyList()
        );
    }
}