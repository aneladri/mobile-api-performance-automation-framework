package core.ai.healing;

import io.appium.java_client.AppiumDriver;

public abstract class HealingBaseScreen {

    protected final AppiumDriver driver;

    protected HealingBaseScreen(
            AppiumDriver driver
    ) {
        this.driver = driver;
    }
}
