package mobile.capabilities;

import core.config.ConfigManager;
import org.openqa.selenium.remote.DesiredCapabilities;

public final class IosCapabilities {

    private IosCapabilities() {
    }

    public static DesiredCapabilities build() {

        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("platformName", "iOS");
        capabilities.setCapability("appium:automationName", "XCUITest");

        capabilities.setCapability(
                "appium:deviceName",
                ConfigManager.getRequired("iosDeviceName")
        );

        capabilities.setCapability(
                "appium:platformVersion",
                ConfigManager.getRequired("iosPlatformVersion")
        );

        capabilities.setCapability("appium:udid", ConfigManager.getRequired("iosUdid"));
        capabilities.setCapability("appium:showXcodeLog", true);
        capabilities.setCapability("appium:wdaLaunchTimeout", 120000);
        capabilities.setCapability("appium:wdaConnectionTimeout", 120000);

        String iosBundleId = ConfigManager.get("iosBundleId");
        String iosAppPath = ConfigManager.get("iosAppPath");

        if (iosBundleId != null && !iosBundleId.isBlank()) {
            capabilities.setCapability("appium:bundleId", iosBundleId);
        } else {
            capabilities.setCapability("appium:app", iosAppPath);
        }

        return capabilities;
    }
}