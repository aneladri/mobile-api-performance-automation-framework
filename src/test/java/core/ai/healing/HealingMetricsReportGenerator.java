package core.ai.healing;

public class HealingMetricsReportGenerator {

    private static final double ESTIMATED_COST_PER_CLAUDE_CALL = 0.01;

    public String generate(HealingMetrics metrics) {

        int avoidedCalls =
                metrics.getCacheHits()
                        + metrics.getRuleHits()
                        + metrics.getBudgetBlocks();

        double estimatedCostSaved =
                avoidedCalls * ESTIMATED_COST_PER_CLAUDE_CALL;

        return """
                # Healing Metrics Report

                Healing Attempts:
                %d

                Cache Hits:
                %d

                Rule Hits:
                %d

                Budget Blocks:
                %d

                Claude Escalations:
                %d

                Claude Recommendations Accepted:
                %d

                Estimated API Calls Avoided:
                %d

                Estimated Cost Saved:
                $%.2f
                """.formatted(
                metrics.getHealingAttempts(),
                metrics.getCacheHits(),
                metrics.getRuleHits(),
                metrics.getBudgetBlocks(),
                metrics.getClaudeCalls(),
                metrics.getClaudeHits(),
                avoidedCalls,
                estimatedCostSaved
        );
    }
}
