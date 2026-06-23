package core.ai.healing;

public final class HealingMetricsCollector {

    private static HealingMetrics metrics =
            new HealingMetrics();

    private HealingMetricsCollector() {
    }

    public static HealingMetrics getMetrics() {
        return metrics;
    }

    public static void recordHealingAttempt() {
        metrics.incrementHealingAttempts();
    }

    public static void recordCacheHit() {
        metrics.incrementCacheHits();
    }

    public static void recordRuleHit() {
        metrics.incrementRuleHits();
    }

    public static void recordBudgetBlock() {
        metrics.incrementBudgetBlocks();
    }

    public static void recordClaudeCall() {
        metrics.incrementClaudeCalls();
    }

    public static void reset() {
        metrics = new HealingMetrics();
    }
}