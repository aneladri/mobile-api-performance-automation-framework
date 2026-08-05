package roomscan.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import roomscan.automation.RoomScanAutomation;
import roomscan.capture.CaptureResult;
import roomscan.capture.MockCaptureProvider;
import roomscan.fixtures.ScanFixtureManager;
import roomscan.flows.RoomScanFlow;
import roomscan.workflow.ScanWorkflow;
import roomscan.workflow.ScanWorkflowValidator;

public class RoomScanMockWorkflowTest {

    @Test
    public void verifyMockRoomScanCompletesAndProcesses() {

        MockCaptureProvider captureProvider =
                new MockCaptureProvider();

        ScanWorkflow workflow =
                new ScanWorkflow();

        RoomScanFlow flow =
                new RoomScanFlow(
                        captureProvider,
                        workflow
                );

        RoomScanAutomation automation =
                new RoomScanAutomation(
                        flow
                );

        CaptureResult result =
                automation.performMockRoomScan(
                        ScanFixtureManager.SMALL_BEDROOM_CLEAN
                );

        Assert.assertNotNull(
                result,
                "Capture result should not be null"
        );

        Assert.assertNotNull(
                result.getScanId(),
                "Scan ID should be generated"
        );

        Assert.assertEquals(
                result.getFixtureName(),
                ScanFixtureManager.SMALL_BEDROOM_CLEAN,
                "Fixture name mismatch"
        );

        ScanWorkflowValidator.assertProcessed(
                workflow
        );
    }
}
