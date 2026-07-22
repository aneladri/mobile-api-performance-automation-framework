package ai.tests;

import core.ai.AgentResponse;
import core.ai.FailureReportGenerator;
import core.ai.ReportWriter;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class ReportWriterTest {

    @Test
    public void verifyAiReportIsWrittenToFile() {

        AgentResponse response =
                new AgentResponse();

        response.setClassification(
                "SSL Configuration Failure"
        );

        response.setRootCause(
                "Certificate not trusted"
        );

        response.setRecommendation(
                "Import certificates into trust store"
        );

        response.setConfidence(95);

        FailureReportGenerator generator =
                new FailureReportGenerator();

        String report =
                generator.generateReport(response);

        Path reportPath =
                ReportWriter.writeReport(
                        report,
                        "failure-analysis-report.md"
                );

        Assert.assertTrue(
                Files.exists(reportPath),
                "AI report file should exist"
        );
    }
}
