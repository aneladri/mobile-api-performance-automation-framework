package mobile.screens.ios;

import io.appium.java_client.AppiumDriver;

public class IOSSettingsScreen {

    private final AppiumDriver driver;

    public IOSSettingsScreen(AppiumDriver driver) {
        this.driver = driver;
    }

    public boolean isLoaded() {
        return driver != null;
    }

    public String getTitle() {
        return "Settings";
    }
}
