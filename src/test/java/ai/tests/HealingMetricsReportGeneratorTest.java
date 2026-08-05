package ai.tests;

import core.ai.ReportWriter;
import core.ai.healing.HealingMetrics;
import core.ai.healing.HealingMetricsReportGenerator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class HealingMetricsReportGeneratorTest {

    @Test
    public void verifyHealingMetricsReportIsGenerated() {

        HealingMetrics metrics =
                new HealingMetrics();

        metrics.incrementHealingAttempts();
        metrics.incrementCacheHits();
        metrics.incrementRuleHits();
        metrics.incrementBudgetBlocks();
        metrics.incrementClaudeCalls();

        HealingMetricsReportGenerator generator =
                new HealingMetricsReportGenerator();

        String report =
                generator.generate(metrics);

        Path reportPath =
                ReportWriter.writeReport(
                        report,
                        "healing-metrics-report.md"
                );

        Assert.assertTrue(
                Files.exists(reportPath),
                "Healing metrics report should be written"
        );

        Assert.assertTrue(
                report.contains("Healing Metrics Report")
        );

        Assert.assertTrue(
                report.contains("Estimated Cost Saved")
        );
    }
}
