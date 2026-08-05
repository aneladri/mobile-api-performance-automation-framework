package mobile.utils;

import core.driver.DriverManager;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.Capabilities;

public final class DeviceInfoUtils {

    private DeviceInfoUtils() {
    }

    public static String getPlatformName() {
        return getCapability("platformName", "Unknown");
    }

    public static String getPlatformVersion() {
        return getCapability("platformVersion", "Unknown");
    }

    public static String getDeviceName() {
        String value = getCapability("deviceName", null);

        if (value != null) {
            return value;
        }

        return getCapability("appium:deviceName", "Unknown");
    }

    public static String getAutomationName() {
        String value = getCapability("automationName", null);

        if (value != null) {
            return value;
        }

        return getCapability("appium:automationName", "Unknown");
    }

    public static String getSessionId() {
        AppiumDriver driver = DriverManager.getDriver();

        if (driver == null || driver.getSessionId() == null) {
            return "Unknown";
        }

        return driver.getSessionId().toString();
    }

    private static String getCapability(
            String capabilityName,
            String defaultValue) {

        AppiumDriver driver = DriverManager.getDriver();

        if (driver == null) {
            return defaultValue;
        }

        Capabilities capabilities = driver.getCapabilities();

        Object value = capabilities.getCapability(capabilityName);

        return value == null
                ? defaultValue
                : String.valueOf(value);
    }
}
