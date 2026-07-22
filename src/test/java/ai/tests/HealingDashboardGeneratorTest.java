package ai.tests;

import core.ai.HealingDashboardGenerator;
import core.ai.ReportWriter;
import core.ai.UnifiedHealingAdvisor;
import core.ai.UnifiedHealingRecommendation;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class HealingDashboardGeneratorTest {

    @Test
    public void verifyHealingDashboardIsGenerated() {

        UnifiedHealingAdvisor advisor =
                new UnifiedHealingAdvisor();

        UnifiedHealingRecommendation recommendation =
                advisor.analyze(
                        "NoSuchElementException: Unable to locate element"
                );

        HealingDashboardGenerator generator =
                new HealingDashboardGenerator();

        String dashboard =
                generator.generate(recommendation);

        Path path =
                ReportWriter.writeReport(
                        dashboard,
                        "healing-dashboard.md"
                );

        Assert.assertTrue(
                Files.exists(path),
                "Healing dashboard should be written"
        );

        Assert.assertTrue(
                dashboard.contains("MAPAF Healing Dashboard")
        );

        Assert.assertTrue(
                dashboard.contains("accessibilityId")
        );
    }
}
