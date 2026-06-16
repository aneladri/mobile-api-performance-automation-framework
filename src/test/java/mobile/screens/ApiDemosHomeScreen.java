package mobile.screens;

import mobile.screens.base.BaseScreen;
import org.openqa.selenium.By;

public class ApiDemosHomeScreen extends BaseScreen {

    private final By accessibilityOption =
            By.xpath("//android.widget.TextView[@text='Accessibility']");

    public boolean isHomeScreenDisplayed() {
        return isDisplayed(accessibilityOption);
    }
}
