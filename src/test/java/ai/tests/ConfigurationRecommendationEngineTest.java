package ai.tests;

import core.ai.ConfigurationRecommendation;
import core.ai.ConfigurationRecommendationEngine;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ConfigurationRecommendationEngineTest {

    @Test
    public void verifyAndroidSdkConfigurationRecommendation() {

        ConfigurationRecommendationEngine engine =
                new ConfigurationRecommendationEngine();

        ConfigurationRecommendation recommendation =
                engine.recommend(
                        "Neither ANDROID_HOME nor ANDROID_SDK_ROOT environment variable was exported"
                );

        Assert.assertEquals(
                recommendation.getFailureType(),
                "Android SDK Configuration Failure"
        );

        Assert.assertTrue(
                recommendation.getSuggestedFix()
                        .contains("Android SDK")
        );
    }

    @Test
    public void verifyIosSimulatorConfigurationRecommendation() {

        ConfigurationRecommendationEngine engine =
                new ConfigurationRecommendationEngine();

        ConfigurationRecommendation recommendation =
                engine.recommend(
                        "Unable to find a destination matching the provided destination specifier"
                );

        Assert.assertEquals(
                recommendation.getFailureType(),
                "iOS Simulator Configuration Failure"
        );

        Assert.assertTrue(
                recommendation.getValidationCommand()
                        .contains("simctl")
        );
    }

    @Test
    public void verifyBrowserStackConfigurationRecommendation() {

        ConfigurationRecommendationEngine engine =
                new ConfigurationRecommendationEngine();

        ConfigurationRecommendation recommendation =
                engine.recommend(
                        "BROWSERSTACK_USERNAME is missing"
                );

        Assert.assertEquals(
                recommendation.getFailureType(),
                "BrowserStack Credentials Configuration Failure"
        );
    }

    @Test
    public void verifyApplicationPathRecommendation() {

        ConfigurationRecommendationEngine engine =
                new ConfigurationRecommendationEngine();

        ConfigurationRecommendation recommendation =
                engine.recommend(
                        "Invalid app path"
                );

        Assert.assertEquals(
                recommendation.getFailureType(),
                "Application Path Configuration Failure"
        );
    }
}
