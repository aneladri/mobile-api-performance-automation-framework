package mobile.screens.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;

public class IOSGeneralScreen {

    private final AppiumDriver driver;

    private final By aboutOption =
            AppiumBy.accessibilityId("About");

    public IOSGeneralScreen(AppiumDriver driver) {
        this.driver = driver;
    }

    public boolean isAboutVisible() {
        return !driver.findElements(aboutOption).isEmpty();
    }

    public void tapAbout() {
        driver.findElement(aboutOption).click();
    }
}
