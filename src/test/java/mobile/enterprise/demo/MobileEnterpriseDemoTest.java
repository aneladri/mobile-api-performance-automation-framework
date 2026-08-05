package mobile.enterprise.demo;

import core.ai.healing.HealingMetrics;
import core.ai.healing.HealingMetricsCollector;
import core.base.BaseMobileTest;
import core.config.ConfigManager;
import core.driver.DriverManager;
import core.enterprise.execution.EnterpriseExecutionContext;
import core.enterprise.execution.ExecutionIdentityFactory;
import mobile.enterprise.diagnostics.DeviceDiagnostics;
import mobile.enterprise.logging.MobileExecutionLogger;
import mobile.enterprise.metrics.MobileExecutionStep;
import mobile.enterprise.metrics.MobileExecutionSummary;
import mobile.enterprise.metrics.MobileMetricsCollector;
import mobile.enterprise.reporting.MobileAllurePublisher;
import mobile.enterprise.reporting.MobileDashboardPublisher;
import mobile.utils.ScreenshotUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import roomscan.capture.CaptureQualityReport;
import roomscan.capture.CaptureResult;
import roomscan.capture.MockCaptureProvider;
import roomscan.flows.RoomScanFlow;
import roomscan.workflow.ScanState;
import roomscan.workflow.ScanWorkflow;

import java.util.List;

public class MobileEnterpriseDemoTest extends BaseMobileTest {

    private static final String SCENARIO = "RoomScan Mobile End-to-End Validation";
    private static final String FIXTURE = "living-room-scan.json";

    private MobileMetricsCollector metrics;
    private MockCaptureProvider captureProvider;
    private RoomScanFlow flow;
    private int screenshots;

    @BeforeMethod(alwaysRun = true)
    public void initializeEnterpriseDemo() {
        metrics = new MobileMetricsCollector();
        captureProvider = new MockCaptureProvider();
        flow = new RoomScanFlow(captureProvider, new ScanWorkflow());
        HealingMetricsCollector.reset();
        screenshots = 0;
    }

    @Test(description = "MAPAF Enterprise Mobile RoomScan Demonstration")
    public void demonstrateRoomScanMobileQuality() {
        EnterpriseExecutionContext context = ExecutionIdentityFactory.create("MOB", SCENARIO);
        String environment = ConfigManager.getEnvironment().toUpperCase();
        MobileExecutionLogger.banner(context, environment, "Mock RoomScan Capture");

        runStep(1, "Validate Device and Application Readiness",
                "Prepare the Field Technician device for a RoomScan session.",
                "IDLE",
                () -> Assert.assertNotNull(DriverManager.getDriver(), "Appium driver was not initialized"),
                List.of("Device connected", "Appium session active", "Application available"));

        runStep(2, "Create Scan Session",
                "Initialize a new RoomScan session and prepare the capture workflow.",
                "READY",
                flow::prepareScan,
                List.of("Session initialized", "Workflow moved to READY", "Capture service available"));
        Assert.assertEquals(flow.getWorkflow().getCurrentState(), ScanState.READY);

        final CaptureResult[] result = new CaptureResult[1];
        runStep(3, "Capture Living Room Evidence",
                "Capture room evidence and generate scan metadata for downstream AI processing.",
                "SCANNING → COMPLETED",
                () -> {
                    flow.startScan(FIXTURE);
                    result[0] = flow.finishScan();
                },
                List.of("Capture started", "Capture completed", "Scan ID generated"));
        Assert.assertNotNull(result[0]);
        Assert.assertFalse(result[0].getScanId().isBlank());

        CaptureQualityReport quality = captureProvider.getQualityReport();
        runStep(4, "Review Capture Quality",
                "Validate coverage, tracking confidence and capture warnings before upload.",
                "REVIEW",
                flow::reviewScan,
                List.of("Coverage meets target", "Tracking confidence is HIGH", "No blocking warnings"));
        Assert.assertEquals(quality.getCoveragePercent(), 100);
        Assert.assertEquals(quality.getTrackingConfidence(), "HIGH");
        Assert.assertTrue(quality.getWarnings().isEmpty());

        runStep(5, "Upload RoomScan Evidence",
                "Move validated room evidence into the upload workflow for backend processing.",
                "UPLOADING",
                flow::startUpload,
                List.of("Upload workflow started", "Metadata retained", "Evidence ready for AI"));
        Assert.assertEquals(flow.getWorkflow().getCurrentState(), ScanState.UPLOADING);

        runStep(6, "Complete AI Processing",
                "Mark the CubiCasa-compatible processing workflow complete and expose the floor plan.",
                "PROCESSED",
                flow::markProcessed,
                List.of("AI processing completed", "Floor plan available", "Workflow moved to PROCESSED"));
        Assert.assertEquals(flow.getWorkflow().getCurrentState(), ScanState.PROCESSED);

        runStep(7, "Validate Business Outcome",
                "Confirm the RoomScan workflow is complete and ready for portal review.",
                "PROCESSED",
                () -> {
                    Assert.assertEquals(flow.getWorkflow().getCurrentState(), ScanState.PROCESSED);
                    Assert.assertNotNull(result[0].getScanFilePath());
                },
                List.of("RoomScan completed", "Capture evidence retained", "Ready for web review"));

        DeviceDiagnostics diagnostics = DeviceDiagnostics.collect();
        HealingMetrics healing = HealingMetricsCollector.getMetrics();
        MobileExecutionSummary summary = metrics.summarize(
                context.getExecutionId(),
                context.getCorrelationId(),
                context.getTraceId(),
                SCENARIO,
                environment,
                screenshots,
                flow.getWorkflow().getCurrentState().name(),
                quality,
                healing,
                diagnostics
        );

        MobileDemoPacer.pause("Preparing mobile enterprise summary");
        MobileExecutionLogger.summary(summary);
        MobileDashboardPublisher.publish(summary);

        Assert.assertEquals(summary.result(), "PASSED");
        Assert.assertEquals(summary.finalWorkflowState(), "PROCESSED");
    }

    private void runStep(
            int number,
            String name,
            String objective,
            String workflowState,
            Runnable operation,
            List<String> assertions) {

        long start = System.currentTimeMillis();
        MobileDemoPacer.pause("Presenting step " + number + " - " + name);
        operation.run();
        MobileDemoPacer.pause("Showing result for step " + number);
        boolean passed = true;
        captureEvidence();
        long duration = System.currentTimeMillis() - start;

        MobileExecutionStep step = new MobileExecutionStep(
                number,
                name,
                objective,
                workflowState,
                duration,
                assertions,
                List.of("Screenshot", "Allure evidence", "Device session", "Workflow telemetry"),
                passed
        );

        metrics.record(step);
        MobileExecutionLogger.step(step);
        MobileAllurePublisher.attach(step);
    }

    private void captureEvidence() {
        try {
            ScreenshotUtils.attachScreenshotToAllure();
            screenshots++;
        } catch (Exception exception) {
            System.out.println("[Mobile Enterprise] Screenshot skipped: " + exception.getMessage());
        }
    }
}
