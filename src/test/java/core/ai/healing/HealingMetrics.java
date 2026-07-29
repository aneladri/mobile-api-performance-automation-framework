package core.ai.healing;

public class HealingMetrics {

    private int healingAttempts;
    private int cacheHits;
    private int ruleHits;
    private int budgetBlocks;
    private int claudeCalls;
    private int claudeHits;

    /*
     * AI telemetry
     */
    private int aiCandidatesGenerated;
    private int aiCandidatesAttempted;
    private int successfulCandidateRank;
    private int successfulCandidateConfidence;
    private long totalAiHealingDurationMillis;

    public void incrementHealingAttempts() {
        healingAttempts++;
    }

    public void incrementCacheHits() {
        cacheHits++;
    }

    public void incrementRuleHits() {
        ruleHits++;
    }

    public void incrementBudgetBlocks() {
        budgetBlocks++;
    }

    public void incrementClaudeCalls() {
        claudeCalls++;
    }

    public void incrementClaudeHits() {
        claudeHits++;
    }

    public void addAiCandidatesGenerated(int count) {
        aiCandidatesGenerated += count;
    }

    public void incrementAiCandidateAttempt() {
        aiCandidatesAttempted++;
    }

    public void recordSuccessfulCandidate(
            int rank,
            int confidence) {

        successfulCandidateRank = rank;
        successfulCandidateConfidence = confidence;
    }

    public void addAiHealingDuration(
            long durationMillis) {

        totalAiHealingDurationMillis += durationMillis;
    }

    public int getHealingAttempts() {
        return healingAttempts;
    }

    public int getCacheHits() {
        return cacheHits;
    }

    public int getRuleHits() {
        return ruleHits;
    }

    public int getBudgetBlocks() {
        return budgetBlocks;
    }

    public int getClaudeCalls() {
        return claudeCalls;
    }

    public int getClaudeHits() {
        return claudeHits;
    }

    public int getAiCandidatesGenerated() {
        return aiCandidatesGenerated;
    }

    public int getAiCandidatesAttempted() {
        return aiCandidatesAttempted;
    }

    public int getSuccessfulCandidateRank() {
        return successfulCandidateRank;
    }

    public int getSuccessfulCandidateConfidence() {
        return successfulCandidateConfidence;
    }

    public long getTotalAiHealingDurationMillis() {
        return totalAiHealingDurationMillis;
    }
}