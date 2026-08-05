package web.enterprise.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import web.enterprise.diagnostics.BrowserDiagnostics;
import web.enterprise.metrics.WebExecutionStep;
import web.enterprise.metrics.WebExecutionSummary;
import web.enterprise.metrics.WebMetricsCollector;

import java.util.List;

public class WebMetricsCollectorTest {

    @Test
    public void summarizesPassedRoomScanWorkflow() {
        WebMetricsCollector collector = new WebMetricsCollector();
        collector.record(new WebExecutionStep(
                1,
                "Authenticate",
                "Access portal",
                "Login",
                "Sign in",
                120,
                List.of("Dashboard displayed"),
                List.of("Screenshot"),
                true
        ));
        collector.record(new WebExecutionStep(
                2,
                "Submit Scan",
                "Publish approved scan",
                "Review",
                "Submit",
                80,
                List.of("Dashboard updated"),
                List.of("Trace"),
                true
        ));

        BrowserDiagnostics diagnostics = new BrowserDiagnostics(
                "CHROMIUM", true, 0, 0, 5, 0,
                "PASS", "GENERATED", "GENERATED"
        );
        WebExecutionSummary summary = collector.summarize(
                "WEB-1", "COR-1", "TRACE-1", "RoomScan", "QA",
                "CHROMIUM", 2, diagnostics
        );

        Assert.assertEquals(summary.totalSteps(), 2);
        Assert.assertEquals(summary.passedSteps(), 2);
        Assert.assertEquals(summary.failedSteps(), 0);
        Assert.assertEquals(summary.assertions(), 2);
        Assert.assertEquals(summary.result(), "PASSED");
        Assert.assertEquals(summary.successRate(), 100.0);
    }
}
