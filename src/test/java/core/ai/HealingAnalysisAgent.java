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

        UnifiedHealingRecommendation recommendation =
                advisor.analyze(input);

        AgentMetricsCollector.record(
                getName(),
                recommendation.getConfidence(),
                recommendation.getConfidence() < 50
        );

        return advisor.generateSummary(input);
    }
}