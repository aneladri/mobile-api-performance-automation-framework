package mobile.gestures;

import core.driver.DriverManager;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

public final class GestureUtils {

    private GestureUtils() {
    }

    private static AppiumDriver getDriver() {
        return DriverManager.getDriver();
    }

    public static void tap(WebElement element) {
        element.click();
    }

    public static void scrollDown() {
        Dimension size = getDriver().manage().window().getSize();

        int startX = size.width / 2;
        int startY = (int) (size.height * 0.8);
        int endY = (int) (size.height * 0.2);

        getDriver().executeScript(
                "mobile: scrollGesture",
                java.util.Map.of(
                        "left", startX,
                        "top", endY,
                        "width", startX,
                        "height", startY,
                        "direction", "down",
                        "percent", 0.75
                )
        );
    }

    public static void scrollUp() {
        Dimension size = getDriver().manage().window().getSize();

        int startX = size.width / 2;
        int startY = (int) (size.height * 0.2);
        int endY = (int) (size.height * 0.8);

        getDriver().executeScript(
                "mobile: scrollGesture",
                java.util.Map.of(
                        "left", startX,
                        "top", startY,
                        "width", startX,
                        "height", endY,
                        "direction", "up",
                        "percent", 0.75
                )
        );
    }

    public static void longPress(WebElement element) {
        Point location = element.getLocation();

        getDriver().executeScript(
                "mobile: longClickGesture",
                java.util.Map.of(
                        "x", location.getX(),
                        "y", location.getY(),
                        "duration", 1000
                )
        );
    }
}
