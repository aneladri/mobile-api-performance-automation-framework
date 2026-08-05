package core.ai;

public class SelfHealingRuntime {

    private final SuggestionEngine suggestionEngine =
            new SuggestionEngine();

    public HealingRecommendation analyze(String failureLog) {
        return suggestionEngine.recommend(failureLog);
    }

    public String generateFixSummary(String failureLog) {

        HealingRecommendation recommendation =
                analyze(failureLog);

        return """
                # Self-Healing Recommendation

                Failure Type:
                %s

                Possible Cause:
                %s

                Suggested Fix:
                %s

                Suggested Strategy:
                %s

                Confidence:
                %d
                """.formatted(
                recommendation.getFailureType(),
                recommendation.getPossibleCause(),
                recommendation.getSuggestedFix(),
                recommendation.getSuggestedStrategy(),
                recommendation.getConfidence()
        );
    }
}
