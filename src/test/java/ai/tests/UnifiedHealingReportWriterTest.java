package ai.tests;

import core.ai.ReportWriter;
import core.ai.UnifiedHealingAdvisor;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class UnifiedHealingReportWriterTest {

    @Test
    public void verifyUnifiedHealingReportIsWrittenToFile() {

        UnifiedHealingAdvisor advisor =
                new UnifiedHealingAdvisor();

        String report =
                advisor.generateSummary(
                        "NoSuchElementException: Unable to locate element"
                );

        Path reportPath =
                ReportWriter.writeReport(
                        report,
                        "healing-report.md"
                );

        Assert.assertTrue(
                Files.exists(reportPath),
                "Healing report should be written"
        );

        Assert.assertTrue(
                report.contains("Unified Healing Recommendation")
        );

        Assert.assertTrue(
                report.contains("accessibilityId")
        );
    }
}
