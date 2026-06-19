package mobile.screens.ios;

import io.appium.java_client.AppiumDriver;

public class IOSHomeScreen {

    private final AppiumDriver driver;

    public IOSHomeScreen(AppiumDriver driver) {
        this.driver = driver;
    }

    public boolean isLoaded() {
        return driver != null;
    }
}
