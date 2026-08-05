package dashboard.enterprise.tests;

import dashboard.enterprise.details.ExecutionDetailsHtmlWriter;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class ExecutionDetailsHtmlWriterTest {

    @Test
    public void shouldGenerateModulePageWithPercentCssAndDetailedSteps() throws Exception {
        Path directory = Files.createTempDirectory("mapaf-dashboard-details");
        Path summary = directory.resolve("summary.json");
        Path output = directory.resolve("mobile.html");

        Files.writeString(summary, """
                {
                  "executionId": "MOB-001",
                  "correlationId": "CORR-001",
                  "scenario": "RoomScan Mobile End-to-End Validation",
                  "environment": "QA",
                  "steps": 1,
                  "passedSteps": 1,
                  "failedSteps": 0,
                  "assertions": 3,
                  "screenshots": 1,
                  "successRate": 100.0,
                  "totalDurationMillis": 1500,
                  "result": "PASSED",
                  "executionSteps": [
                    {
                      "number": 1,
                      "name": "Validate Device",
                      "businessObjective": "Prepare device",
                      "workflowState": "READY",
                      "durationMillis": 100,
                      "assertions": ["Device connected"],
                      "evidence": ["Screenshot"],
                      "passed": true
                    }
                  ]
                }
                """);

        new ExecutionDetailsHtmlWriter().write(
                "mobile",
                "RoomScan Mobile",
                summary,
                output
        );

        String html = Files.readString(output);
        Assert.assertTrue(html.contains("RoomScan Mobile"));
        Assert.assertTrue(html.contains("Validate Device"));
        Assert.assertTrue(html.contains("Success Rate"));
        Assert.assertTrue(html.contains("100.00%"));
        Assert.assertTrue(html.contains("mobile.html"));
    }
}
