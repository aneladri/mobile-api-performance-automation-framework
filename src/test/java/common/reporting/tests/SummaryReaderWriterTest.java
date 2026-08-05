package common.reporting.tests;

import common.reporting.io.SummaryReader;
import common.reporting.io.SummaryWriter;
import common.reporting.model.ExecutionEnvironment;
import common.reporting.model.ExecutionMetrics;
import common.reporting.model.ExecutionStatus;
import common.reporting.model.ExecutionSummary;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class SummaryReaderWriterTest {

    @Test
    public void shouldWriteAndReadExecutionSummary() throws Exception {
        ExecutionMetrics metrics = ExecutionMetrics.fromCounts(
                10,
                9,
                1,
                0,
                15
        );

        ExecutionEnvironment environment = new ExecutionEnvironment(
                "QA",
                "100",
                "sprint-1-reporting-contract",
                "local",
                "api-001"
        );

        ExecutionSummary expected = new ExecutionSummary(
                "API",
                ExecutionStatus.FAIL,
                metrics,
                environment
        );

        expected.addDetail("suite", "API Regression");
        expected.addLink("report", "build/reports/tests/apiTest/index.html");

        Path outputFile = Files.createTempDirectory("mapaf-summary-test")
                .resolve("summary.json");

        Path writtenFile = SummaryWriter.write(expected, outputFile);
        ExecutionSummary actual = SummaryReader.read(writtenFile);

        Assert.assertTrue(Files.exists(writtenFile));
        Assert.assertEquals(actual.getModule(), "API");
        Assert.assertEquals(actual.getStatus(), ExecutionStatus.FAIL);
        Assert.assertEquals(actual.getMetrics().getTotal(), 10);
        Assert.assertEquals(actual.getMetrics().getPassed(), 9);
        Assert.assertEquals(actual.getMetrics().getFailed(), 1);
        Assert.assertEquals(actual.getMetrics().getPassRate(), 90.0);
        Assert.assertEquals(
                actual.getEnvironment().getEnvironment(),
                "QA"
        );
        Assert.assertEquals(
                actual.getDetails().get("suite"),
                "API Regression"
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Execution summary module must not be blank"
    )
    public void shouldRejectSummaryWithoutModule() throws Exception {
        ExecutionSummary invalidSummary = new ExecutionSummary(
                "",
                ExecutionStatus.PASS,
                ExecutionMetrics.fromCounts(1, 1, 0, 0, 1),
                new ExecutionEnvironment(
                        "QA",
                        "100",
                        "main",
                        "local",
                        "invalid-001"
                )
        );

        Path outputFile = Files.createTempDirectory("mapaf-invalid-test")
                .resolve("summary.json");

        SummaryWriter.write(invalidSummary, outputFile);
    }
}
