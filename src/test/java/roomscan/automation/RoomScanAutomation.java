package roomscan.automation;

import roomscan.capture.CaptureProvider;
import roomscan.capture.CaptureResult;
import roomscan.flows.RoomScanFlow;

public class RoomScanAutomation {

    private final RoomScanFlow flow;

    public RoomScanAutomation(
            RoomScanFlow flow
    ) {
        this.flow = flow;
    }

    public CaptureResult performMockRoomScan(
            String fixtureName
    ) {

        flow.prepareScan();

        flow.startScan(
                fixtureName
        );

        CaptureResult result =
                flow.finishScan();

        flow.reviewScan();

        flow.startUpload();

        flow.markProcessed();

        return result;
    }
}
