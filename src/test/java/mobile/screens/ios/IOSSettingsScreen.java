package mobile.screens.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;

public class IOSSettingsScreen {

    private final AppiumDriver driver;

    private final By settingsTitle =
            AppiumBy.accessibilityId("Settings");

    private final By searchField =
            AppiumBy.iOSClassChain(
                    "**/XCUIElementTypeSearchField"
            );

    public IOSSettingsScreen(AppiumDriver driver) {
        this.driver = driver;
    }

    public boolean isLoaded() {
        return !driver.findElements(settingsTitle).isEmpty();
    }

    public boolean isSearchFieldVisible() {
        return !driver.findElements(searchField).isEmpty();
    }
}