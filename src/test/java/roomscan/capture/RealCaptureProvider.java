package roomscan.capture;

public class RealCaptureProvider implements CaptureProvider {

    @Override
    public void startCapture(String fixtureName) {
        throw new UnsupportedOperationException(
                "Real capture is controlled by the application and device hardware"
        );
    }

    @Override
    public boolean isCaptureComplete() {
        throw new UnsupportedOperationException(
                "Real capture completion should be validated through UI state"
        );
    }

    @Override
    public CaptureResult finishCapture() {
        throw new UnsupportedOperationException(
                "Real capture result should be retrieved from app/backend"
        );
    }

    @Override
    public void cancelCapture() {
        throw new UnsupportedOperationException(
                "Real capture cancellation should be performed through UI"
        );
    }

    @Override
    public CaptureQualityReport getQualityReport() {
        throw new UnsupportedOperationException(
                "Real capture quality should be retrieved from app/backend"
        );
    }
}
