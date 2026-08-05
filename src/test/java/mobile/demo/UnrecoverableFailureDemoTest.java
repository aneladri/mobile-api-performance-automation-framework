package mobile.demo;

import core.base.BaseMobileTest;
import mobile.utils.ScreenshotUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

/**
 * Leadership demo: a deliberate, deterministic failure - NOT a broken locator,
 * NOT dependent on healing or live AI behavior. Navigates to the real Views
 * category screen (working locators, no healing involved), reads the real
 * text of the first row ("Animation"), and asserts it equals a wrong expected
 * value ("Views") on purpose. This models a realistic, common real-world
 * failure mode - an assertion mismatch - and fails identically every run,
 * every time, regardless of any AI behavior.
 *
 * Does NOT override the screenshot @AfterMethod the way the other demo tests
 * do - this is intentional. BaseMobileTest's own default
 * captureScreenshotAndTearDown() already attaches a screenshot on FAILURE,
 * so this test exercises that native path directly rather than needing our
 * demo-specific always-run override.
 */
public class UnrecoverableFailureDemoTest extends BaseMobileTest {

    @Test(description = "Deliberate assertion-mismatch failure, for showing a real failure report")
    public void intentionalAssertionMismatchFailsCleanly() {
        ApiDemosNavigationFlow flow = new ApiDemosNavigationFlow();
        ViewsCategoryScreen viewsCategoryScreen = flow.openViewsCategory();

        String actualFirstItemText = viewsCategoryScreen.getFirstItemText();

        Assert.assertEquals(
                actualFirstItemText,
                "Views",
                "Intentional demo failure: expected the first row to say 'Views' but the app "
                        + "correctly shows 'Animation' - this mismatch is deliberate, not a real bug."
        );
    }
}
