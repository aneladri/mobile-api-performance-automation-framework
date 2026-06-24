package core.ai.healing;

import mobile.screens.base.BaseScreen;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public abstract class HealingBaseScreen extends BaseScreen {

    private final LocatorHealingEngine healingEngine =
            new LocatorHealingEngine();

    @Override
    protected WebElement find(By locator) {
        try {
            return super.find(locator);
        } catch (Exception e) {
            WebElement healed =
                    healingEngine.heal(
                            locator,
                            getClass().getSimpleName(),
                            Thread.currentThread().getStackTrace()[2].getMethodName()
                    );

            if (healed != null) {
                return healed;
            }

            throw e;
        }
    }
}