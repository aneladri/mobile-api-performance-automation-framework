package core.ai;

public class LocatorRecommendationEngine {

    public LocatorRecommendation recommend(String failureLog) {

        LocatorRecommendation recommendation =
                new LocatorRecommendation();

        if (failureLog == null || failureLog.isBlank()) {
            recommendation.setFailureType("Unknown Locator Failure");
            recommendation.setSuggestedLocator("Manual review required");
            recommendation.setAlternativeLocator("Manual review required");
            recommendation.setSuggestedWait("Manual review required");
            recommendation.setConfidence(0);
            return recommendation;
        }

        if (failureLog.contains("NoSuchElementException")) {
            recommendation.setFailureType("Locator Not Found");
            recommendation.setSuggestedLocator("accessibilityId=<stable-accessibility-id>");
            recommendation.setAlternativeLocator("iOSClassChain or UiSelector based on visible text");
            recommendation.setSuggestedWait("Wait for element visibility before interaction");
            recommendation.setConfidence(88);
            return recommendation;
        }

        if (failureLog.contains("TimeoutException")) {
            recommendation.setFailureType("Element Synchronization Issue");
            recommendation.setSuggestedLocator("Reuse existing locator after wait validation");
            recommendation.setAlternativeLocator("Validate element appears after screen transition");
            recommendation.setSuggestedWait("Use explicit wait for visibility or clickability");
            recommendation.setConfidence(84);
            return recommendation;
        }

        if (failureLog.contains("StaleElementReferenceException")) {
            recommendation.setFailureType("Stale Element Reference");
            recommendation.setSuggestedLocator("Re-locate element immediately before action");
            recommendation.setAlternativeLocator("Avoid storing WebElement references");
            recommendation.setSuggestedWait("Wait for screen stability before interaction");
            recommendation.setConfidence(82);
            return recommendation;
        }

        recommendation.setFailureType("Unknown Locator Failure");
        recommendation.setSuggestedLocator("Manual locator review required");
        recommendation.setAlternativeLocator("Inspect application DOM/source");
        recommendation.setSuggestedWait("Add wait only after confirming locator stability");
        recommendation.setConfidence(40);

        return recommendation;
    }
}
