package mobile.demo;

import core.base.BaseMobileTest;
import mobile.utils.ScreenshotUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

/**
 * Leadership demo: a real, hand-written (not AI-generated) multi-step
 * Appium navigation flow against the actual ApiDemos app - main category
 * list -> tap "Views" -> confirm arrival on the Views submenu, using content
 * unique to that screen since its action bar title does not change.
 *
 * Every locator here was confirmed from a live uiautomator dump tonight, not
 * guessed - see ApiDemosMainScreen / ViewsCategoryScreen for the exact dumps
 * that back them.
 */
public class ApiDemosFlowTest extends BaseMobileTest {

    @Test(description = "Appium drives a real multi-step navigation flow through ApiDemos")
    public void navigatesFromMainScreenIntoViewsCategory() {
        ApiDemosNavigationFlow flow = new ApiDemosNavigationFlow();

        ViewsCategoryScreen viewsCategoryScreen = flow.openViewsCategory();

        Assert.assertTrue(
                viewsCategoryScreen.isViewsCategoryScreenDisplayed(),
                "Expected the Views category screen to be displayed after tapping 'Views', but it was not."
        );
    }

    @AfterMethod(alwaysRun = true)
    public void attachScreenshotRegardlessOfOutcome() {
        try {
            ScreenshotUtils.attachScreenshotToAllure();
        } catch (Exception e) {
            System.out.println(
                    "[Demo] Screenshot capture skipped: " + e.getMessage()
            );
        }
    }
}
