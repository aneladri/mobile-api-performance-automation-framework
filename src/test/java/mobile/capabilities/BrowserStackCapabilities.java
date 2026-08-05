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

        capabilities.setCapability("platformName", "Android");

        Map<String, Object> browserstackOptions =
                new HashMap<>();

        browserstackOptions.put(
                "deviceName",
                ConfigManager.getRequired("browserstackDeviceName")
        );

        browserstackOptions.put(
                "osVersion",
                ConfigManager.getRequired("browserstackOsVersion")
        );

        capabilities.setCapability(
                "bstack:options",
                browserstackOptions
        );

        capabilities.setCapability(
                "appium:app",
                ConfigManager.getRequired("browserstackAppId")
        );

        return capabilities;
    }

    public static DesiredCapabilities buildIos() {

        DesiredCapabilities capabilities =
                new DesiredCapabilities();

        capabilities.setCapability("platformName", "iOS");

        Map<String, Object> browserstackOptions =
                new HashMap<>();

        browserstackOptions.put(
                "deviceName",
                ConfigManager.getRequired("browserstackIosDeviceName")
        );

        browserstackOptions.put(
                "osVersion",
                ConfigManager.getRequired("browserstackIosOsVersion")
        );

        capabilities.setCapability(
                "bstack:options",
                browserstackOptions
        );

        capabilities.setCapability(
                "appium:app",
                ConfigManager.getRequired("browserstackIosAppId")
        );

        return capabilities;
    }
}