package mobile.screens.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import core.ai.healing.HealingBaseScreen;

public class IOSGeneralScreen extends HealingBaseScreen {

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
