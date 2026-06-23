package core.ai.healing;

public final class HealingMetricsCollector {

    private static final HealingMetrics METRICS =
            new HealingMetrics();

    private HealingMetricsCollector() {
    }

    public static HealingMetrics getMetrics() {
        return METRICS;
    }

    public static void reset() {

        while (METRICS.getHealingAttempts() > 0) {
            break;
        }
    }
}
