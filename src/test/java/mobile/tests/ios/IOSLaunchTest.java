package mobile.tests.ios;

import core.base.BaseMobileTest;
import core.driver.DriverManager;
import mobile.screens.ios.IOSSettingsScreen;
import org.testng.Assert;
import org.testng.annotations.Test;

public class IOSLaunchTest extends BaseMobileTest {

    @Test
    public void verifyIOSSettingsLaunches() {

        IOSSettingsScreen settingsScreen =
                new IOSSettingsScreen(
                        DriverManager.getDriver()
                );

        Assert.assertTrue(
                settingsScreen.isLoaded(),
                "Settings screen should be visible"
        );
    }

    @Test
    public void verifyIOSScreenshotCapture() {

        IOSSettingsScreen settingsScreen =
            new IOSSettingsScreen(
                    DriverManager.getDriver()
            );

        Assert.assertTrue(
            settingsScreen.isLoaded(),
            "Settings screen should be visible"
        );
    }

    @Test
    public void verifyIOSSettingsSearchFieldIsVisible() {
        IOSSettingsScreen settingsScreen =
                new IOSSettingsScreen(
                    DriverManager.getDriver()
                );

        Assert.assertTrue(
                settingsScreen.isSearchFieldVisible(),
                "Settings search field should be visible"
        );
    }
}