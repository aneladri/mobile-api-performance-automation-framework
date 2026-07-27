package core.ai.healing;

public class HealingMetrics {

    private int healingAttempts;
    private int cacheHits;
    private int ruleHits;
    private int budgetBlocks;
    private int claudeCalls;
    private int claudeHits;

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
}
