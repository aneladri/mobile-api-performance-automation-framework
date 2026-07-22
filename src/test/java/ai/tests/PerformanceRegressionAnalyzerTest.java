package ai.tests;

import core.ai.PerformanceBaseline;
import core.ai.PerformanceRegressionAnalyzer;
import core.ai.ReportWriter;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class PerformanceRegressionAnalyzerTest {

    @Test
    public void verifyPerformancePassClassification() {

        PerformanceBaseline baseline =
                new PerformanceBaseline(
                        40.46,
                        0.00,
                        2.98
                );

        PerformanceRegressionAnalyzer analyzer =
                new PerformanceRegressionAnalyzer();

        String classification =
                analyzer.classify(
                        baseline,
                        42.00,
                        0.00,
                        2.90
                );

        Assert.assertEquals(
                classification,
                "PASS"
        );
    }

    @Test
    public void verifyPerformanceWarningClassification() {

        PerformanceBaseline baseline =
                new PerformanceBaseline(
                        40.46,
                        0.00,
                        2.98
                );

        PerformanceRegressionAnalyzer analyzer =
                new PerformanceRegressionAnalyzer();

        String classification =
                analyzer.classify(
                        baseline,
                        52.00,
                        0.00,
                        2.90
                );

        Assert.assertEquals(
                classification,
                "WARNING"
        );
    }

    @Test
    public void verifyPerformanceRegressionClassification() {

        PerformanceBaseline baseline =
                new PerformanceBaseline(
                        40.46,
                        0.00,
                        2.98
                );

        PerformanceRegressionAnalyzer analyzer =
                new PerformanceRegressionAnalyzer();

        String classification =
                analyzer.classify(
                        baseline,
                        70.00,
                        0.00,
                        2.90
                );

        Assert.assertEquals(
                classification,
                "REGRESSION"
        );
    }

    @Test
    public void verifyPerformanceReportIsWritten() {

        PerformanceBaseline baseline =
                new PerformanceBaseline(
                        40.46,
                        0.00,
                        2.98
                );

        PerformanceRegressionAnalyzer analyzer =
                new PerformanceRegressionAnalyzer();

        String report =
                analyzer.generateReport(
                        baseline,
                        70.00,
                        0.00,
                        2.90
                );

        Path reportPath =
                ReportWriter.writeReport(
                        report,
                        "performance-regression-report.md"
                );

        Assert.assertTrue(
                Files.exists(reportPath),
                "Performance regression report should be written"
        );

        Assert.assertTrue(
                report.contains("REGRESSION")
        );
    }
}
