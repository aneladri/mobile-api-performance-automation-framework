package ai.tests;

import core.ai.AgentResponse;
import core.ai.FailureAnalysisRuntime;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FailureAnalysisRuntimeTest {

    @Test
    public void verifySslFailureAnalysis() {

        FailureAnalysisRuntime runtime =
                new FailureAnalysisRuntime();

        AgentResponse response =
                runtime.analyze(
                        "SSLHandshakeException"
                );

        Assert.assertEquals(
                response.getClassification(),
                "SSL Configuration Failure"
        );

        Assert.assertEquals(
                response.getConfidence(),
                95
        );
    }

    @Test
    public void verifyBrowserStackLimitFailureAnalysis() {
        FailureAnalysisRuntime runtime = new FailureAnalysisRuntime();

        AgentResponse response =
                runtime.analyze("BROWSERSTACK_TESTING_TIME_LIMIT_EXHAUSTED");

        Assert.assertEquals(
            response.getClassification(),
            "BrowserStack Account Limit Failure"
        );
    }

     @Test
     public void verifyAndroidHomeFailureAnalysis() {
         FailureAnalysisRuntime runtime = new FailureAnalysisRuntime();

         AgentResponse response =
            runtime.analyze("ANDROID_HOME is not set");

         Assert.assertEquals(
            response.getClassification(),
            "Android Environment Configuration Failure"
        );
   }

     @Test
     public void verifyXcodeBuildFailureAnalysis() {
        FailureAnalysisRuntime runtime = new FailureAnalysisRuntime();

        AgentResponse response =
            runtime.analyze("xcodebuild failed with code 70 WebDriverAgent");

        Assert.assertEquals(
            response.getClassification(),
            "iOS WebDriverAgent Failure"
        );
    }

     @Test
     public void verifyAppiumSessionFailureAnalysis() {
        FailureAnalysisRuntime runtime = new FailureAnalysisRuntime();

        AgentResponse response =
            runtime.analyze("SessionNotCreatedException Could not find a connected Android device");

        Assert.assertEquals(
            response.getClassification(),
            "Appium Session Creation Failure"
        );
    }
}
