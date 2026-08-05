package mobile.capabilities;

import core.config.ConfigManager;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

        String configuredAppPath =
                ConfigManager.getRequired("androidAppPath");

        Path appPath =
                Paths.get(configuredAppPath)
                        .toAbsolutePath()
                        .normalize();

        if (!Files.exists(appPath)) {
            throw new IllegalStateException(
                    "Android application was not found: " + appPath
            );
        }

        capabilities.setCapability(
                "appium:app",
                appPath.toString()
        );

        capabilities.setCapability(
                "appium:newCommandTimeout",
                120
        );

        return capabilities;
    }
}