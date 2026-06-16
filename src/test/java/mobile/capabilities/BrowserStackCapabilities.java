package mobile.capabilities;

import core.config.ConfigManager;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.HashMap;
import java.util.Map;

public final class BrowserStackCapabilities {

    private BrowserStackCapabilities() {
    }

    public static DesiredCapabilities buildAndroid() {

        DesiredCapabilities capabilities =
                new DesiredCapabilities();

        capabilities.setCapability(
                "platformName",
                "Android"
        );

        Map<String, Object> browserstackOptions =
                new HashMap<>();

        browserstackOptions.put(
                "deviceName",
                ConfigManager.getRequired(
                        "browserstackDeviceName"
                )
        );

        browserstackOptions.put(
                "osVersion",
                ConfigManager.getRequired(
                        "browserstackOsVersion"
                )
        );

        browserstackOptions.put(
                "projectName",
                "MAPAF"
        );

        browserstackOptions.put(
                "buildName",
                "Regression Build"
        );

        browserstackOptions.put(
                "sessionName",
                "Android Test"
        );

        capabilities.setCapability(
                "bstack:options",
                browserstackOptions
        );

        capabilities.setCapability(
                "appium:app",
                ConfigManager.getRequired(
                        "browserstackAppId"
                )
        );

        return capabilities;
    }
}
