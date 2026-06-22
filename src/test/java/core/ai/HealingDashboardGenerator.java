package core.ai;

public class HealingDashboardGenerator {

    public String generate(
            UnifiedHealingRecommendation recommendation
    ) {

        return """
                # MAPAF Healing Dashboard

                ## Locator Recommendation

                %s

                ## Wait Recommendation

                %s

                ## Configuration Recommendation

                %s

                ## Confidence

                %d
                """.formatted(
                recommendation.getLocatorRecommendation(),
                recommendation.getWaitRecommendation(),
                recommendation.getConfigurationRecommendation(),
                recommendation.getConfidence()
        );
    }
}
