package ai.tests;

import core.ai.PerformanceBaseline;
import core.ai.PerformanceReportGenerator;
import core.ai.ReportWriter;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class PerformanceReportGeneratorTest {

    @Test
    public void verifyPerformanceReportIsGeneratedFromK6Summary() {

        Path summaryPath =
                Path.of(
                        "performance",
                        "results",
                        "smoke-summary.json"
                );

        PerformanceBaseline baseline =
                new PerformanceBaseline(
                        40.46,
                        0.00,
                        2.98
                );

        PerformanceReportGenerator generator =
                new PerformanceReportGenerator();

        String report =
                generator.generate(
                        summaryPath,
                        baseline
                );

        Path reportPath =
                ReportWriter.writeReport(
                        report,
                        "performance-analysis-report.md"
                );

        Assert.assertTrue(
                Files.exists(reportPath),
                "Performance analysis report should be written"
        );

        Assert.assertTrue(
                report.contains("Performance Analysis Report")
        );

        Assert.assertTrue(
                report.contains("Classification")
        );

        Assert.assertTrue(
                report.contains("Recommendation")
        );
    }
}
