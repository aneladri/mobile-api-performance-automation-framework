package core.driver;

import core.config.BrowserStackConfig;
import core.config.ConfigManager;
import core.config.ExecutionConfig;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import mobile.capabilities.AndroidCapabilities;
import mobile.capabilities.BrowserStackCapabilities;
import mobile.capabilities.IosCapabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.time.Duration;

public class DriverFactory {

    private DriverFactory() {
    }

    public static void createDriver(String platform) {
        try {
            AppiumDriver driver;

            if (platform.equalsIgnoreCase("android")) {
                DesiredCapabilities capabilities;
                String serverUrl;

                if (ExecutionConfig.isBrowserStack()) {
                    capabilities = BrowserStackCapabilities.buildAndroid();
                    serverUrl = BrowserStackConfig.getHubUrl();
                } else {
                    capabilities = AndroidCapabilities.build();
                    serverUrl = ConfigManager.getRequired("appiumServerUrl");
                }

                driver = new AndroidDriver(new URL(serverUrl), capabilities);

            } else if (platform.equalsIgnoreCase("ios")) {
                DesiredCapabilities capabilities = IosCapabilities.build();

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