package dashboard.enterprise.tests;

import dashboard.enterprise.aggregation.EnterpriseSummaryAdapter;
import dashboard.enterprise.model.EnterpriseModuleView;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class EnterpriseSummaryAdapterTest {

    @Test
    public void shouldReadPerformanceQualityGateAndDurationSeconds() throws Exception {
        Path summary = Files.createTempFile("performance-enterprise-summary", ".json");
        Files.writeString(summary, """
                {
                  "scenario": "RoomScan Performance Validation",
                  "environment": "QA",
                  "requests": 10000,
                  "passed": 10000,
                  "failed": 0,
                  "durationSeconds": 120,
                  "qualityGate": "PASS",
                  "availabilityPercent": 100.0,
                  "errorRatePercent": 0.0,
                  "throughputPerSecond": 83.33,
                  "p95Ms": 420.0
                }
                """);

        EnterpriseModuleView view = new EnterpriseSummaryAdapter().read(
                "performance",
                "RoomScan Performance",
                summary
        );

        Assert.assertEquals(view.status(), "PASS");
        Assert.assertEquals(view.total(), 10000);
        Assert.assertEquals(view.passed(), 10000);
        Assert.assertEquals(view.failed(), 0);
        Assert.assertEquals(view.durationMillis(), 120000L);
        Assert.assertTrue(view.hasRun());
    }
}
