package ai.tests;

import core.ai.OrchestratorRuntime;
import core.ai.ReportWriter;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class OrchestratorReportWriterTest {

    @Test
    public void verifyOrchestratorReportIsWrittenToFile() {

        OrchestratorRuntime runtime =
                new OrchestratorRuntime();

        String report =
                runtime.analyze(
                        "SSLHandshakeException"
                );

        Path reportPath =
                ReportWriter.writeReport(
                        report,
                        "orchestrator-report.md"
                );

        Assert.assertTrue(
                Files.exists(reportPath),
                "Orchestrator report should be written"
        );

        Assert.assertTrue(
                report.contains("Failure Analysis")
        );

        Assert.assertTrue(
                report.contains("Documentation Impact")
        );
    }
}