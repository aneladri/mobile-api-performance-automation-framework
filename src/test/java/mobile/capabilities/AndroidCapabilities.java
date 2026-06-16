package mobile.capabilities;

import core.config.ConfigManager;
import org.openqa.selenium.remote.DesiredCapabilities;

public final class AndroidCapabilities {

    private AndroidCapabilities() {
    }

    public static DesiredCapabilities build() {

        DesiredCapabilities capabilities =
                new DesiredCapabilities();

        capabilities.setCapability(
                "platformName",
                "Android"
        );

        capabilities.setCapability(
                "appium:automationName",
                "UiAutomator2"
        );

        capabilities.setCapability(
                "appium:deviceName",
                ConfigManager.getRequired("androidDeviceName")
        );

        capabilities.setCapability(
                "appium:app",
                ConfigManager.getRequired("androidAppPath")
        );

        return capabilities;
    }
}
