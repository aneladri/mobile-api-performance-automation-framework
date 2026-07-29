package ai.tests;

import core.ai.healing.HealingMetrics;
import core.ai.healing.HealingMetricsCollector;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HealingMetricsCollectorTest {

    @Test
    public void verifyHealingMetricsAvailable() {

        HealingMetricsCollector.reset();

        HealingMetrics metrics =
                HealingMetricsCollector.getMetrics();

        Assert.assertNotNull(metrics);
    }

    @Test
    public void shouldCollectAiTelemetry() {

        HealingMetricsCollector.reset();

        HealingMetricsCollector.recordAiCandidatesGenerated(3);

        HealingMetricsCollector.recordAiCandidateAttempt();

        HealingMetricsCollector.recordAiCandidateAttempt();

        HealingMetricsCollector.recordAiHealingSuccess(
                2,
                91
        );

        HealingMetricsCollector.recordAiHealingDuration(
                275
        );

        HealingMetrics metrics =
                HealingMetricsCollector.getMetrics();

        Assert.assertEquals(
                metrics.getAiCandidatesGenerated(),
                3
        );

        Assert.assertEquals(
                metrics.getAiCandidatesAttempted(),
                2
        );

        Assert.assertEquals(
                metrics.getSuccessfulCandidateRank(),
                2
        );

        Assert.assertEquals(
                metrics.getSuccessfulCandidateConfidence(),
                91
        );

        Assert.assertEquals(
                metrics.getTotalAiHealingDurationMillis(),
                275
        );
    }

    @Test
    public void shouldAccumulateHealingDuration() {

        HealingMetricsCollector.reset();

        HealingMetricsCollector.recordAiHealingDuration(100);

        HealingMetricsCollector.recordAiHealingDuration(200);

        Assert.assertEquals(
                HealingMetricsCollector
                        .getMetrics()
                        .getTotalAiHealingDurationMillis(),
                300
        );
    }
}