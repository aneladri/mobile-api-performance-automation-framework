package roomscan.capture;

public interface CaptureProvider {

    void startCapture(String fixtureName);

    boolean isCaptureComplete();

    CaptureResult finishCapture();

    void cancelCapture();

    CaptureQualityReport getQualityReport();
}
