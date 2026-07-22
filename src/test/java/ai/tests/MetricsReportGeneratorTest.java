package ai.tests;

import core.ai.AgentMetrics;
import core.ai.AgentMetricsCollector;
import core.ai.FailureAnalysisAgent;
import core.ai.MetricsReportGenerator;
import core.ai.ReportWriter;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class MetricsReportGeneratorTest {

    @BeforeMethod
    public void resetMetrics() {
        AgentMetricsCollector.reset();
    }

    @Test
    public void verifyMetricsReportGeneration() {

        FailureAnalysisAgent agent =
                new FailureAnalysisAgent();

        agent.analyze("SSLHandshakeException");
        agent.analyze("UNKNOWN FAILURE SAMPLE");

        AgentMetrics metrics =
                AgentMetricsCollector.getMetrics(
                        "failure-analysis"
                );

        MetricsReportGenerator generator =
                new MetricsReportGenerator();

        String report =
                generator.generate(
                        "failure-analysis",
                        metrics
                );

        Assert.assertTrue(
                report.contains("Total Runs")
        );

        Assert.assertTrue(
                report.contains("2")
        );
    }

    @Test
    public void verifyMetricsReportIsWrittenToFile() {

        FailureAnalysisAgent agent =
                new FailureAnalysisAgent();

        agent.analyze("SSLHandshakeException");

        AgentMetrics metrics =
                AgentMetricsCollector.getMetrics(
                        "failure-analysis"
                );

        MetricsReportGenerator generator =
                new MetricsReportGenerator();

        String report =
                generator.generate(
                        "failure-analysis",
                        metrics
                );

        Path reportPath =
                ReportWriter.writeReport(
                        report,
                        "agent-metrics-report.md"
                );

        Assert.assertTrue(
                Files.exists(reportPath),
                "Metrics report should be written"
        );
    }
}
