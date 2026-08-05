package mobile.tests;

import core.base.BaseMobileTest;
import mobile.screens.ApiDemosHomeScreen;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ApiDemosLaunchTest extends BaseMobileTest {

    @Test
    public void verifyApiDemosLaunches() {
        ApiDemosHomeScreen homeScreen = new ApiDemosHomeScreen();

        Assert.assertTrue(
                homeScreen.isHomeScreenDisplayed(),
                "ApiDemos home screen not displayed"
        );
    }
}
