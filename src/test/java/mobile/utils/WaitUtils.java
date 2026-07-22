package mobile.utils;

import core.driver.DriverManager;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public final class WaitUtils {

    private static final int DEFAULT_TIMEOUT =
            Integer.parseInt(
                    System.getProperty(
                            "waitTimeoutSeconds",
                            "15"
                    )
            );

    private WaitUtils() {
    }

    private static AppiumDriver getDriver() {
        return DriverManager.getDriver();
    }

    private static WebDriverWait wait(int timeoutSeconds) {
        return new WebDriverWait(
                getDriver(),
                Duration.ofSeconds(timeoutSeconds)
        );
    }

    public static WebElement waitForVisible(By locator) {
        return waitForVisible(
                locator,
                DEFAULT_TIMEOUT
        );
    }

    public static WebElement waitForVisible(
            By locator,
            int timeoutSeconds
    ) {
        return wait(timeoutSeconds)
                .until(
                        ExpectedConditions.visibilityOfElementLocated(locator)
                );
    }

    public static WebElement waitForClickable(By locator) {
        return waitForClickable(
                locator,
                DEFAULT_TIMEOUT
        );
    }

    public static WebElement waitForClickable(
            By locator,
            int timeoutSeconds
    ) {
        return wait(timeoutSeconds)
                .until(
                        ExpectedConditions.elementToBeClickable(locator)
                );
    }

    public static boolean waitForInvisibility(By locator) {
        return waitForInvisibility(
                locator,
                DEFAULT_TIMEOUT
        );
    }

    public static boolean waitForInvisibility(
            By locator,
            int timeoutSeconds
    ) {
        return wait(timeoutSeconds)
                .until(
                        ExpectedConditions.invisibilityOfElementLocated(locator)
                );
    }

    public static boolean waitForText(
            By locator,
            String expectedText
    ) {
        return waitForText(
                locator,
                expectedText,
                DEFAULT_TIMEOUT
        );
    }

    public static boolean waitForText(
            By locator,
            String expectedText,
            int timeoutSeconds
    ) {
        return wait(timeoutSeconds)
                .until(
                        ExpectedConditions.textToBe(
                                locator,
                                expectedText
                        )
                );
    }
}