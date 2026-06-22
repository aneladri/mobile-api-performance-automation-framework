package core.ai;

public class WaitStrategyEngine {

    public WaitStrategyRecommendation recommend(String failureLog) {

        WaitStrategyRecommendation recommendation =
                new WaitStrategyRecommendation();

        if (failureLog == null || failureLog.isBlank()) {
            recommendation.setFailureType("Unknown Wait Failure");
            recommendation.setSuggestedWait("Manual review required");
            recommendation.setImplementation("Review logs and screen behavior");
            recommendation.setAlternative("Manual investigation");
            recommendation.setConfidence(0);
            return recommendation;
        }

        if (failureLog.contains("TimeoutException")) {
            recommendation.setFailureType("Synchronization Failure");
            recommendation.setSuggestedWait("Wait for Visibility");
            recommendation.setImplementation("WaitUtils.waitForVisible(locator)");
            recommendation.setAlternative("WaitUtils.waitForClickable(locator)");
            recommendation.setConfidence(90);
            return recommendation;
        }

        if (failureLog.contains("element not clickable")) {
            recommendation.setFailureType("Clickability Failure");
            recommendation.setSuggestedWait("Wait for Clickability");
            recommendation.setImplementation("WaitUtils.waitForClickable(locator)");
            recommendation.setAlternative("Wait for visibility, then tap using GestureUtils");
            recommendation.setConfidence(88);
            return recommendation;
        }

        if (failureLog.contains("stale element")) {
            recommendation.setFailureType("Stale Element Synchronization Failure");
            recommendation.setSuggestedWait("Re-locate Element Before Action");
            recommendation.setImplementation("Find element after wait, not before wait");
            recommendation.setAlternative("Avoid storing WebElement references");
            recommendation.setConfidence(85);
            return recommendation;
        }

        recommendation.setFailureType("Unknown Wait Failure");
        recommendation.setSuggestedWait("Manual review required");
        recommendation.setImplementation("Review synchronization strategy");
        recommendation.setAlternative("Add explicit wait after confirming locator stability");
        recommendation.setConfidence(40);

        return recommendation;
    }
}
