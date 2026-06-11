package core.driver;

import core.config.ConfigManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.time.Duration;

public class DriverFactory {

    private DriverFactory() {
    }

    public static void createDriver(String platform) {
        try {
            DesiredCapabilities capabilities = new DesiredCapabilities();

            AppiumDriver driver;

            if (platform.equalsIgnoreCase("android")) {
                capabilities.setCapability("platformName", "Android");
                capabilities.setCapability("appium:automationName", "UiAutomator2");
                capabilities.setCapability("appium:deviceName", ConfigManager.getRequired("androidDeviceName"));
                capabilities.setCapability("appium:app", ConfigManager.getRequired("androidAppPath"));

                driver = new AndroidDriver(
                        new URL(ConfigManager.getRequired("appiumServerUrl")),
                        capabilities
                );

            } else if (platform.equalsIgnoreCase("ios")) {
                capabilities.setCapability("platformName", "iOS");
                capabilities.setCapability("appium:automationName", "XCUITest");
                capabilities.setCapability("appium:deviceName", ConfigManager.getRequired("iosDeviceName"));
                capabilities.setCapability("appium:app", ConfigManager.getRequired("iosAppPath"));

                driver = new IOSDriver(
                        new URL(ConfigManager.getRequired("appiumServerUrl")),
                        capabilities
                );

            } else {
                throw new RuntimeException("Unsupported mobile platform: " + platform);
            }

            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            DriverManager.setDriver(driver);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create Appium driver for platform: " + platform, e);
        }
    }
}