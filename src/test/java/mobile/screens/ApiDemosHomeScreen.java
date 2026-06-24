package mobile.screens;

import core.ai.healing.HealingBaseScreen;
import org.openqa.selenium.By;

public class ApiDemosHomeScreen extends HealingBaseScreen {

    private final By accessibilityOption =
            By.xpath("//android.widget.TextView[@text='Accessibility']");

    public boolean isHomeScreenDisplayed() {
        return isDisplayed(accessibilityOption);
    }
}
