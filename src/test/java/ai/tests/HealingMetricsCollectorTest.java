package ai.tests;

import core.ai.healing.HealingMetrics;
import core.ai.healing.HealingMetricsCollector;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HealingMetricsCollectorTest {

    @Test
    public void verifyHealingMetricsAvailable() {

        HealingMetrics metrics =
                HealingMetricsCollector.getMetrics();

        Assert.assertNotNull(
                metrics
        );
    }
}
