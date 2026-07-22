package mobile.screens.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import core.ai.healing.HealingBaseScreen;

public class IOSAboutScreen extends HealingBaseScreen {

    private final AppiumDriver driver;

    private final By aboutTitle =
            AppiumBy.accessibilityId("About");

    public IOSAboutScreen(AppiumDriver driver) {
        this.driver = driver;
    }

    public boolean isLoaded() {
        return !driver.findElements(aboutTitle).isEmpty();
    }
}
