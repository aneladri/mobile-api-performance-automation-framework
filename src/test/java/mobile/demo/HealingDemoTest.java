package mobile.demo;

import core.ai.ReportWriter;
import core.ai.healing.HealingMetrics;
import core.ai.healing.HealingMetricsCollector;
import core.ai.healing.HealingMetricsReportGenerator;
import core.base.BaseMobileTest;
import mobile.utils.ScreenshotUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;

import java.nio.file.Path;

/**
 * Leadership demo: exercises the full 3-tier locator healing chain.
 *
 * Two scenarios, same real target element (ApiDemos' "Views" row), broken
 * two different ways:
 *   1. ruleBasedHealingRecoversBrokenLocator (runs first, priority 1) - a
 *      stale-resource-id-but-correct-text break, solved for free by Tier 2's
 *      local rule engine. No AI call happens for this one.
 *   2. selfHealingRecoversBrokenLocator (runs second, priority 2) - an
 *      accessibility-ID break that Tier 2 cannot structurally solve, so it
 *      falls through to Tier 3 (Claude AI). See HealingDemoScreen for exactly
 *      why each locator shape lands on its respective tier.
 *
 * attachScreenshotRegardlessOfOutcome (below) is demo-specific: the
 * framework's real default (BaseMobileTest.captureScreenshotAndTearDown)
 * only attaches a screenshot on FAILURE or SKIP, which is the right policy
 * for real suites but means a successful healing demo would produce zero
 * screenshots to show. This does not change that default - it adds an
 * additional, always-run capture scoped to this demo class only.
 *
 * writeHealingMetricsReport wires HealingMetricsReportGenerator to a REAL
 * run for the first time - previously it was only ever exercised by a unit
 * test with numbers set manually via metrics.incrementCacheHits() etc.
 * This suite's real HealingMetricsCollector counters (populated by the two
 * tests above) are what get written to reports/ai/generated/ this time.
 */
public class HealingDemoTest extends BaseMobileTest {

    @Test(
            priority = 1,
            description = "Tier 2 local rule engine recovers a stale-resource-id locator for free, no AI call"
    )
    public void ruleBasedHealingRecoversBrokenLocator() {
        HealingDemoScreen screen = new HealingDemoScreen();
        screen.tapViewsMenuItemViaStaleResourceId();
    }

    @Test(
            priority = 2,
            description = "Tier 3 AI self-healing recovers an intentionally broken locator that Tier 2 cannot"
    )
    public void selfHealingRecoversBrokenLocator() {
        HealingDemoScreen screen = new HealingDemoScreen();
        screen.tapViewsMenuItem();
    }

    @AfterMethod(alwaysRun = true)
    public void attachScreenshotRegardlessOfOutcome() {
        try {
            ScreenshotUtils.attachScreenshotToAllure();
        } catch (Exception e) {
            System.out.println(
                    "[Demo] Screenshot capture skipped: " + e.getMessage()
            );
        }
    }

    @AfterSuite(alwaysRun = true)
    public static void writeHealingMetricsReport() {
        HealingMetrics metrics =
                HealingMetricsCollector.getMetrics();

        String report =
                new HealingMetricsReportGenerator().generate(metrics);

        Path reportPath =
                ReportWriter.writeReport(report, "healing-metrics-report.md");

        System.out.println(
                "[Demo] Healing metrics report written to: " + reportPath.toAbsolutePath()
        );
    }
}
