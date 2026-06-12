package mobile.utils;

import core.driver.DriverManager;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public final class WaitUtils {

    private static final int DEFAULT_TIMEOUT = 15;

    private WaitUtils() {
    }

    private static AppiumDriver getDriver() {
        return DriverManager.getDriver();
    }

    private static WebDriverWait wait(int timeout) {
        return new WebDriverWait(
                getDriver(),
                Duration.ofSeconds(timeout)
        );
    }

    public static WebElement waitForVisible(By locator) {

        return wait(DEFAULT_TIMEOUT)
                .until(
                        ExpectedConditions.visibilityOfElementLocated(locator)
                );
    }

    public static WebElement waitForClickable(By locator) {

        return wait(DEFAULT_TIMEOUT)
                .until(
                        ExpectedConditions.elementToBeClickable(locator)
                );
    }

    public static boolean waitForInvisibility(By locator) {

        return wait(DEFAULT_TIMEOUT)
                .until(
                        ExpectedConditions.invisibilityOfElementLocated(locator)
                );
    }

    public static boolean waitForText(
            By locator,
            String expectedText) {

        return wait(DEFAULT_TIMEOUT)
                .until(
                        ExpectedConditions.textToBe(locator, expectedText)
                );
    }
}
