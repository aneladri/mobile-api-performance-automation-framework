package mobile.capabilities;

import core.config.ConfigManager;
import org.openqa.selenium.remote.DesiredCapabilities;

public final class IosCapabilities {

    private IosCapabilities() {
    }

    public static DesiredCapabilities build() {

        DesiredCapabilities capabilities =
                new DesiredCapabilities();

        capabilities.setCapability(
                "platformName",
                "iOS"
        );

        capabilities.setCapability(
                "appium:automationName",
                "XCUITest"
        );

        capabilities.setCapability(
                "appium:deviceName",
                ConfigManager.getRequired("iosDeviceName")
        );

        capabilities.setCapability(
                "appium:app",
                ConfigManager.getRequired("iosAppPath")
        );

        return capabilities;
    }
}
