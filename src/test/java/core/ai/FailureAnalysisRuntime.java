package core.ai;

public class FailureAnalysisRuntime {

    public AgentResponse analyze(String log) {

        AgentResponse response = new AgentResponse();

        if (log == null || log.isBlank()) {
            response.setClassification("Unknown Failure");
            response.setRootCause("No log content provided");
            response.setRecommendation("Provide failure log input");
            response.setConfidence(0);
            return response;
        }

        if (log.contains("SSLHandshakeException")
                || log.contains("PKIX path building failed")) {

            response.setClassification("SSL Configuration Failure");
            response.setRootCause("Certificate not trusted by Java trust store");
            response.setRecommendation("Import certificates into Java cacerts trust store");
            response.setConfidence(95);
            return response;
        }

        if (log.contains("BROWSERSTACK_TESTING_TIME_LIMIT_EXHAUSTED")) {

            response.setClassification("BrowserStack Account Limit Failure");
            response.setRootCause("BrowserStack testing minutes are exhausted");
            response.setRecommendation("Renew BrowserStack plan or use local execution");
            response.setConfidence(99);
            return response;
        }

        if (log.contains("ANDROID_HOME")
                || log.contains("ANDROID_SDK_ROOT")) {

            response.setClassification("Android Environment Configuration Failure");
            response.setRootCause("Android SDK environment variable is missing or invalid");
            response.setRecommendation("Set ANDROID_HOME or ANDROID_SDK_ROOT correctly");
            response.setConfidence(90);
            return response;
        }

        if (log.contains("xcodebuild failed")
                || log.contains("WebDriverAgent")) {

            response.setClassification("iOS WebDriverAgent Failure");
            response.setRootCause("Xcode, simulator runtime, or WebDriverAgent startup issue");
            response.setRecommendation("Verify Xcode setup, simulator runtime, and XCUITest driver");
            response.setConfidence(90);
            return response;
        }

        if (log.contains("SessionNotCreatedException")
                || log.contains("Could not find a connected Android device")) {

            response.setClassification("Appium Session Creation Failure");
            response.setRootCause("Appium could not create a device session");
            response.setRecommendation("Verify Appium server, device/emulator, platform config, and capabilities");
            response.setConfidence(85);
            return response;
        }

        response.setClassification("Unknown Failure");
        response.setRootCause("No known failure pattern matched");
        response.setRecommendation("Review logs manually and add new pattern to FailureAnalysisRuntime");
        response.setConfidence(40);

        return response;
    }
}