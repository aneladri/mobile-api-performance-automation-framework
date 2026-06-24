package mobile.screens.ios;

import io.appium.java_client.AppiumDriver;
import core.ai.healing.HealingBaseScreen;

public class IOSHomeScreen extends HealingBaseScreen{

    private final AppiumDriver driver;

    public IOSHomeScreen(AppiumDriver driver) {
        this.driver = driver;
    }

    public boolean isLoaded() {
        return driver != null;
    }
}
