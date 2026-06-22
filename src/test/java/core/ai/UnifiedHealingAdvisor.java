package core.ai;

public class UnifiedHealingAdvisor {

    private final LocatorRecommendationEngine locatorEngine =
            new LocatorRecommendationEngine();

    private final WaitStrategyEngine waitEngine =
            new WaitStrategyEngine();

    private final ConfigurationRecommendationEngine configEngine =
            new ConfigurationRecommendationEngine();

    public UnifiedHealingRecommendation analyze(String failureLog) {

        LocatorRecommendation locator =
                locatorEngine.recommend(failureLog);

        WaitStrategyRecommendation wait =
                waitEngine.recommend(failureLog);

        ConfigurationRecommendation config =
                configEngine.recommend(failureLog);

        UnifiedHealingRecommendation recommendation =
                new UnifiedHealingRecommendation();

        recommendation.setLocatorRecommendation(
                locator.getSuggestedLocator()
        );

        recommendation.setWaitRecommendation(
                wait.getImplementation()
        );

        recommendation.setConfigurationRecommendation(
                config.getSuggestedFix()
        );

        recommendation.setConfidence(
                Math.max(
                        locator.getConfidence(),
                        Math.max(
                                wait.getConfidence(),
                                config.getConfidence()
                        )
                )
        );

        return recommendation;
    }

    public String generateSummary(String failureLog) {

        UnifiedHealingRecommendation recommendation =
                analyze(failureLog);

        return """
                # Unified Healing Recommendation

                Locator Recommendation:
                %s

                Wait Recommendation:
                %s

                Configuration Recommendation:
                %s

                Confidence:
                %d
                """.formatted(
                recommendation.getLocatorRecommendation(),
                recommendation.getWaitRecommendation(),
                recommendation.getConfigurationRecommendation(),
                recommendation.getConfidence()
        );
    }
}
