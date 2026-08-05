package roomscan.flows;

import roomscan.capture.CaptureProvider;
import roomscan.capture.CaptureResult;
import roomscan.workflow.ScanState;
import roomscan.workflow.ScanWorkflow;

public class RoomScanFlow {

    private final CaptureProvider captureProvider;
    private final ScanWorkflow workflow;

    public RoomScanFlow(
            CaptureProvider captureProvider,
            ScanWorkflow workflow
    ) {
        this.captureProvider = captureProvider;
        this.workflow = workflow;
    }

    public void prepareScan() {
        workflow.moveTo(
                ScanState.READY
        );
    }

    public void startScan(String fixtureName) {
        workflow.moveTo(
                ScanState.SCANNING
        );

        captureProvider.startCapture(
                fixtureName
        );
    }

    public CaptureResult finishScan() {
        if (!captureProvider.isCaptureComplete()) {
            throw new IllegalStateException(
                    "Capture is not complete"
            );
        }

        CaptureResult result =
                captureProvider.finishCapture();

        workflow.moveTo(
                ScanState.COMPLETED
        );

        return result;
    }

    public void reviewScan() {
        workflow.moveTo(
                ScanState.REVIEW
        );
    }

    public void startUpload() {
        workflow.moveTo(
                ScanState.UPLOADING
        );
    }

    public void markProcessed() {
        workflow.moveTo(
                ScanState.PROCESSED
        );
    }

    public void failScan() {
        workflow.moveTo(
                ScanState.FAILED
        );
    }

    public void cancelScan() {
        workflow.moveTo(
                ScanState.CANCELLED
        );

        captureProvider.cancelCapture();
    }

    public ScanWorkflow getWorkflow() {
        return workflow;
    }
}
