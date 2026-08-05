package core.ai;

public class ConfigurationRecommendationEngine {

    public ConfigurationRecommendation recommend(String failureLog) {

        ConfigurationRecommendation recommendation =
                new ConfigurationRecommendation();

        if (failureLog == null || failureLog.isBlank()) {
            recommendation.setFailureType("Unknown Configuration Failure");
            recommendation.setMissingConfiguration("Unknown");
            recommendation.setSuggestedFix("Provide failure log input");
            recommendation.setValidationCommand("Manual review required");
            recommendation.setConfidence(0);
            return recommendation;
        }

        if (failureLog.contains("ANDROID_HOME")
                || failureLog.contains("ANDROID_SDK_ROOT")) {

            recommendation.setFailureType("Android SDK Configuration Failure");
            recommendation.setMissingConfiguration("ANDROID_HOME or ANDROID_SDK_ROOT");
            recommendation.setSuggestedFix("Set Android SDK environment variables correctly");
            recommendation.setValidationCommand("echo $ANDROID_HOME && adb version");
            recommendation.setConfidence(95);
            return recommendation;
        }

        if (failureLog.contains("iosUdid")
                || failureLog.contains("Unable to find a destination matching")) {

            recommendation.setFailureType("iOS Simulator Configuration Failure");
            recommendation.setMissingConfiguration("iosUdid or simulator runtime");
            recommendation.setSuggestedFix("Update iosUdid and verify simulator runtime is installed");
            recommendation.setValidationCommand("xcrun simctl list devices | grep Booted");
            recommendation.setConfidence(90);
            return recommendation;
        }

        if (failureLog.contains("BROWSERSTACK_USERNAME")
                || failureLog.contains("BROWSERSTACK_ACCESS_KEY")) {

            recommendation.setFailureType("BrowserStack Credentials Configuration Failure");
            recommendation.setMissingConfiguration("BrowserStack username or access key");
            recommendation.setSuggestedFix("Export BrowserStack credentials or configure GitHub Secrets");
            recommendation.setValidationCommand("echo $BROWSERSTACK_USERNAME");
            recommendation.setConfidence(95);
            return recommendation;
        }

        if (failureLog.contains("Invalid app path")
                || failureLog.contains("app path")) {

            recommendation.setFailureType("Application Path Configuration Failure");
            recommendation.setMissingConfiguration("androidAppPath or iosAppPath");
            recommendation.setSuggestedFix("Verify application path exists under apps/ directory");
            recommendation.setValidationCommand("ls apps/android apps/ios");
            recommendation.setConfidence(88);
            return recommendation;
        }

        if (failureLog.contains("platform")
                || failureLog.contains("Unsupported platform")) {

            recommendation.setFailureType("Platform Configuration Failure");
            recommendation.setMissingConfiguration("platform");
            recommendation.setSuggestedFix("Set platform to android or ios");
            recommendation.setValidationCommand("grep platform env/qa.properties");
            recommendation.setConfidence(85);
            return recommendation;
        }

        recommendation.setFailureType("Unknown Configuration Failure");
        recommendation.setMissingConfiguration("Unknown");
        recommendation.setSuggestedFix("Review env properties and runtime configuration");
        recommendation.setValidationCommand("Manual review required");
        recommendation.setConfidence(40);

        return recommendation;
    }
}
