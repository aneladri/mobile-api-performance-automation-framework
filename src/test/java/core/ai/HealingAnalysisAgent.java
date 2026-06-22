package core.ai;

public class HealingAnalysisAgent implements Agent {

    private final UnifiedHealingAdvisor advisor =
            new UnifiedHealingAdvisor();

    @Override
    public String getName() {
        return "healing-analysis";
    }

    @Override
    public String analyze(String input) {
        return advisor.generateSummary(input);
    }
}
