package mobile.demo;

import core.ai.healing.HealingBaseScreen;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Screen Object: the "Views" category submenu.
 *
 * Locators confirmed from a live `adb shell uiautomator dump` taken after
 * tapping "Views" on a freshly-launched ApiDemos session tonight.
 *
 * Important, confirmed-by-testing note: this screen's action bar title stays
 * "API Demos" - it does NOT change to "Views" - so arrival on this screen
 * cannot be confirmed via the title. Instead, arrival is confirmed via a row
 * that exists on THIS screen but not on the main category list ("Buttons"),
 * which is a reliable, content-based way to distinguish the two screens.
 */
public class ViewsCategoryScreen extends HealingBaseScreen {

    private static final By BUTTONS_ROW =
            AppiumBy.accessibilityId("Buttons");

    private static final By ANIMATION_ROW =
            AppiumBy.accessibilityId("Animation");

    public boolean isViewsCategoryScreenDisplayed() {
        return isDisplayed(BUTTONS_ROW) && isDisplayed(ANIMATION_ROW);
    }

    /**
     * Returns the real, current text of the first row in the Views category
     * list ("Animation" per tonight's confirmed dump). Uses a locator that
     * resolves immediately, on purpose - no healing involved in this call at
     * all, so a test built around this method fails or passes deterministically,
     * with zero dependency on live AI behavior.
     */
    public String getFirstItemText() {
        return getText(ANIMATION_ROW);
    }
}
