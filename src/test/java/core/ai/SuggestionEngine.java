package core.ai;

public class SuggestionEngine {

    public HealingRecommendation recommend(String failureLog) {

        HealingRecommendation recommendation =
                new HealingRecommendation();

        if (failureLog == null || failureLog.isBlank()) {
            recommendation.setFailureType("Unknown Failure");
            recommendation.setPossibleCause("No failure log provided");
            recommendation.setSuggestedFix("Provide valid failure log input");
            recommendation.setSuggestedStrategy("Manual review required");
            recommendation.setConfidence(0);
            return recommendation;
        }

        if (failureLog.contains("NoSuchElementException")) {
            recommendation.setFailureType("Locator Failure");
            recommendation.setPossibleCause("Element locator may be invalid, changed, or element is not visible");
            recommendation.setSuggestedFix("Review locator strategy and confirm element availability");
            recommendation.setSuggestedStrategy("Prefer accessibilityId; fallback to iOSClassChain or UiSelector");
            recommendation.setConfidence(85);
            return recommendation;
        }

        if (failureLog.contains("TimeoutException")) {
            recommendation.setFailureType("Synchronization Failure");
            recommendation.setPossibleCause("Element did not become available within timeout");
            recommendation.setSuggestedFix("Add explicit wait for visibility/clickability");
            recommendation.setSuggestedStrategy("Use WaitUtils before interacting with element");
            recommendation.setConfidence(82);
            return recommendation;
        }

        if (failureLog.contains("StaleElementReferenceException")) {
            recommendation.setFailureType("Stale Element Failure");
            recommendation.setPossibleCause("Element reference became invalid after screen refresh or navigation");
            recommendation.setSuggestedFix("Re-locate element immediately before interaction");
            recommendation.setSuggestedStrategy("Avoid storing WebElement references for long-lived screen states");
            recommendation.setConfidence(80);
            return recommendation;
        }

        recommendation.setFailureType("Unknown Failure");
        recommendation.setPossibleCause("No known self-healing pattern matched");
        recommendation.setSuggestedFix("Review logs manually and add new pattern to SuggestionEngine");
        recommendation.setSuggestedStrategy("Manual investigation");
        recommendation.setConfidence(40);

        return recommendation;
    }
}
