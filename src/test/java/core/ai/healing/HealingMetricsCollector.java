package core.ai.healing;

public final class HealingMetricsCollector {

    private static HealingMetrics metrics = new HealingMetrics();

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

    public static void recordClaudeHit() {
        metrics.incrementClaudeHits();
    }

    public static void reset() {
        metrics = new HealingMetrics();
    }

    public static void recordAiCandidatesGenerated(
            int count) {

        metrics.addAiCandidatesGenerated(count);
    }

    public static void recordAiCandidateAttempt() {

        metrics.incrementAiCandidateAttempt();
    }

    public static void recordAiHealingSuccess(
            int rank,
            int confidence) {

        metrics.recordSuccessfulCandidate(
                rank,
                confidence);
    }

    public static void recordAiHealingDuration(
            long durationMillis) {

        metrics.addAiHealingDuration(
                durationMillis);
    }
}