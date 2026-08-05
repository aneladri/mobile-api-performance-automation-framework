package ai.tests;

import core.ai.AgentExecutionHistory;
import core.ai.AgentHistoryReportGenerator;
import core.ai.ExecutionRecord;
import core.ai.ReportWriter;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class AgentHistoryReportGeneratorTest {

    @BeforeMethod
    public void resetHistory() {
        AgentExecutionHistory.reset();
    }

    @Test
    public void verifyAgentHistoryReportIsGenerated() {

        AgentExecutionHistory.record(
                new ExecutionRecord(
                        "failure-analysis",
                        "SSL Configuration Failure",
                        95
                )
        );

        AgentExecutionHistory.record(
                new ExecutionRecord(
                        "failure-analysis",
                        "BrowserStack Account Limit Failure",
                        99
                )
        );

        AgentHistoryReportGenerator generator =
                new AgentHistoryReportGenerator();

        String report =
                generator.generate(
                        AgentExecutionHistory.getRecords()
                );

        Path reportPath =
                ReportWriter.writeReport(
                        report,
                        "agent-history-report.md"
                );

        Assert.assertTrue(
                Files.exists(reportPath),
                "Agent history report should be written"
        );

        Assert.assertTrue(
                report.contains("SSL Configuration Failure")
        );
    }
}
