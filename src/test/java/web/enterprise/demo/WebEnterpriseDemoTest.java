package web.enterprise.demo;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import core.enterprise.execution.EnterpriseExecutionContext;
import core.enterprise.execution.ExecutionIdentityFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import web.config.WebConfiguration;
import web.enterprise.diagnostics.BrowserDiagnostics;
import web.enterprise.diagnostics.NetworkEvidenceCollector;
import web.enterprise.flow.RoomScanPortalFlow;
import web.enterprise.logging.WebExecutionLogger;
import web.enterprise.metrics.WebExecutionStep;
import web.enterprise.metrics.WebExecutionSummary;
import web.enterprise.metrics.WebMetricsCollector;
import web.enterprise.pages.RoomScanPortalPage;
import web.enterprise.reporting.WebAllurePublisher;
import web.enterprise.reporting.WebDashboardPublisher;
import web.tests.base.BaseWebTest;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class WebEnterpriseDemoTest extends BaseWebTest {

    private static final String SCENARIO = "RoomScan Portal End-to-End Validation";
    private static final String ENVIRONMENT = "QA";

    @Override
    protected WebConfiguration createWebConfiguration() {
        WebConfiguration configuration = super.createWebConfiguration();
        configuration.setTraceEnabled(true);
        configuration.setSaveTraceOnSuccess(true);
        configuration.setVideoEnabled(true);
        configuration.setConsoleLogsEnabled(true);
        return configuration;
    }

    @Test(description = "MAPAF Enterprise Playwright Demonstration")
    public void demonstrateRoomScanPortalQuality() {
        EnterpriseExecutionContext context = ExecutionIdentityFactory.create("WEB", SCENARIO);
        WebMetricsCollector metrics = new WebMetricsCollector();
        NetworkEvidenceCollector network = new NetworkEvidenceCollector();
        network.register(page());
        installRoomScanApiRoutes();

        String browserName = System.getProperty("web.browser", "CHROMIUM").toUpperCase();
        WebExecutionLogger.banner(context, ENVIRONMENT, browserName, isHeadless());

        RoomScanPortalPage portal = new RoomScanPortalPage(page());
        RoomScanPortalFlow flow = new RoomScanPortalFlow(portal);
        int screenshots = 0;

        screenshots += runStep(metrics, 1,
                "Open Portal and Authenticate",
                "Confirm the Quality Engineer can securely access the RoomScan operations portal.",
                "Login",
                "Authenticate QA user",
                () -> flow.openAndAuthenticate(),
                () -> portal.isDashboardVisible(),
                List.of("Portal opened", "Authentication API returned success", "Dashboard displayed"));

        screenshots += runStep(metrics, 2,
                "Search Completed Scan",
                "Locate a RoomScan session whose AI processing is complete.",
                "Inspection Dashboard",
                "Search RS-1045",
                () -> flow.locateCompletedScan("RS-1045"),
                () -> portal.getSearchResult().contains("Floor plan ready"),
                List.of("Scan RS-1045 found", "AI processing marked complete", "Floor plan ready"));

        screenshots += runStep(metrics, 3,
                "Review AI Floor Plan",
                "Validate the CubiCasa-generated floor plan and RoomScan metadata.",
                "AI Floor Plan Review",
                "Open floor plan",
                flow::reviewAiFloorPlan,
                () -> portal.isFloorPlanVisible() && portal.getMetadata().contains("CubiCasa"),
                List.of("Floor plan displayed", "Measurements available", "Provider metadata loaded"));

        screenshots += runStep(metrics, 4,
                "Approve and Submit Scan",
                "Approve the AI result and publish the completed RoomScan workflow.",
                "AI Floor Plan Review",
                "Approve and submit",
                () -> {
                    flow.approveAndSubmit();
                    page().waitForCondition(
                            () -> normalizedStatus(portal).contains("dashboard updated"),
                            new Page.WaitForConditionOptions().setTimeout(10_000)
                    );
                },
                () -> normalizedStatus(portal).contains("dashboard updated"),
                List.of("Floor plan approved", "Scan submitted", "Dashboard updated"));

        BrowserDiagnostics diagnostics = BrowserDiagnostics.collect(
                browserName,
                isHeadless(),
                artifacts(),
                network
        );

        WebExecutionSummary summary = metrics.summarize(
                context.getExecutionId(),
                context.getCorrelationId(),
                context.getTraceId(),
                SCENARIO,
                ENVIRONMENT,
                browserName,
                screenshots,
                diagnostics
        );

        WebAllurePublisher.attachNetwork(network);
        DemoPacer.pause("Preparing executive summary");
        WebExecutionLogger.summary(summary);
        WebDashboardPublisher.publish(summary);

        Assert.assertEquals(summary.result(), "PASSED");
        Assert.assertEquals(diagnostics.networkFailures(), 0L);
        Assert.assertEquals(diagnostics.pageErrors(), 0);
    }

    private int runStep(
            WebMetricsCollector metrics,
            int number,
            String name,
            String objective,
            String pageName,
            String action,
            Runnable operation,
            BooleanSupplier validation,
            List<String> assertions) {
        long start = System.currentTimeMillis();
        DemoPacer.pause("Presenting step " + number + " - " + name);
        operation.run();
        DemoPacer.pause("Showing result for step " + number);
        boolean passed = validation.getAsBoolean();
        long duration = System.currentTimeMillis() - start;
        Path screenshot = artifacts().captureStepScreenshot(page(), "step-" + number + "-" + name);
        WebExecutionStep step = new WebExecutionStep(
                number,
                name,
                objective,
                pageName,
                action,
                duration,
                assertions,
                List.of("Screenshot", "Playwright trace", "Video", "Console log", "Network evidence"),
                passed
        );
        metrics.record(step);
        WebExecutionLogger.step(step);
        WebAllurePublisher.attachStep(step, screenshot);
        Assert.assertTrue(passed, name + " failed");
        return screenshot == null ? 0 : 1;
    }

    private String normalizedStatus(RoomScanPortalPage portal) {
        String status = portal.getStatus();
        return status == null ? "" : status.trim().toLowerCase(Locale.ROOT);
    }

    private void installRoomScanApiRoutes() {
        page().route("https://roomscan.local/api/**", route -> {
            String url = route.request().url();
            String body;
            if (url.endsWith("/auth/login")) {
                body = "{\"authenticated\":true,\"role\":\"QUALITY_ENGINEER\"}";
            } else if (url.endsWith("/floorplan")) {
                body = "{\"scanId\":\"RS-1045\",\"provider\":\"CubiCasa\",\"rooms\":3,\"walls\":12}";
            } else if (url.endsWith("/approve")) {
                body = "{\"approved\":true}";
            } else if (url.endsWith("/submit")) {
                body = "{\"submitted\":true,\"dashboardUpdated\":true}";
            } else {
                body = "{\"scanId\":\"RS-1045\",\"status\":\"COMPLETED\",\"floorPlanReady\":true}";
            }
            route.fulfill(new Route.FulfillOptions()
                    .setStatus(200)
                    .setContentType("application/json")
                    .setBody(body));
        });
    }

    @FunctionalInterface
    private interface BooleanSupplier {
        boolean getAsBoolean();
    }
}
