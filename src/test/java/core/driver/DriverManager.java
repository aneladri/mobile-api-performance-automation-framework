package core.driver;

import io.appium.java_client.AppiumDriver;

public class DriverManager {

    private static final ThreadLocal<AppiumDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void setDriver(AppiumDriver driver) {
        DRIVER.set(driver);
    }

    public static AppiumDriver getDriver() {
        return DRIVER.get();
    }

    public static void quitDriver() {
        if (DRIVER.get() != null) {
            DRIVER.get().quit();
            DRIVER.remove();
        }
    }
}