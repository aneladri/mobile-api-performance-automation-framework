package mobile.demo;

import core.ai.healing.HealingBaseScreen;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Screen Object: ApiDemos main category list ("API Demos" title screen).
 *
 * Locators confirmed from a live `adb shell uiautomator dump` against a
 * freshly-launched (pm clear'd) apps/android/ApiDemos-release.apk session -
 * not AI-generated, not guessed.
 */
public class ApiDemosMainScreen extends HealingBaseScreen {

    private static final By VIEWS_CATEGORY_ROW =
            AppiumBy.accessibilityId("Views");

    public void tapViewsCategory() {
        tap(VIEWS_CATEGORY_ROW);
    }

    public boolean isMainScreenDisplayed() {
        return isDisplayed(VIEWS_CATEGORY_ROW);
    }
}
