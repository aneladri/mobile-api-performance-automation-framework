package mobile.enterprise.tests;

import core.ai.healing.HealingMetrics;
import mobile.enterprise.diagnostics.DeviceDiagnostics;
import mobile.enterprise.metrics.MobileExecutionStep;
import mobile.enterprise.metrics.MobileExecutionSummary;
import mobile.enterprise.metrics.MobileMetricsCollector;
import org.testng.Assert;
import org.testng.annotations.Test;
import roomscan.capture.CaptureQualityReport;

import java.util.List;

public class MobileMetricsCollectorTest {

    @Test
    public void shouldCreatePassedMobileSummary() {
        MobileMetricsCollector collector = new MobileMetricsCollector();
        collector.record(new MobileExecutionStep(
                1, "Create Scan", "Create session", "READY", 10,
                List.of("Session created"), List.of("Screenshot"), true));

        MobileExecutionSummary summary = collector.summarize(
                "MOB-1", "COR-1", "TRACE-1", "RoomScan", "QA", 1,
                "PROCESSED", new CaptureQualityReport(100, "HIGH", List.of()),
                new HealingMetrics(),
                new DeviceDiagnostics("Android", "15", "Pixel", "UiAutomator2", "session", "WiFi", "Portrait", "AVAILABLE")
        );

        Assert.assertEquals(summary.result(), "PASSED");
        Assert.assertEquals(summary.successRate(), 100.0);
        Assert.assertEquals(summary.coveragePercent(), 100);
    }
}
