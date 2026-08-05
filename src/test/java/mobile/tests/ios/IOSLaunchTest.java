package mobile.tests.ios;

import core.base.BaseMobileTest;
import core.driver.DriverManager;
import mobile.screens.ios.IOSAboutScreen;
import mobile.screens.ios.IOSGeneralScreen;
import mobile.screens.ios.IOSSettingsScreen;
import org.testng.Assert;
import org.testng.annotations.Test;
import mobile.screens.ios.IOSGeneralScreen;
import mobile.screens.ios.IOSAboutScreen;

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

    @Test
    public void verifyIOSSearchInteraction() {

        IOSSettingsScreen settingsScreen =
            new IOSSettingsScreen(
                    DriverManager.getDriver()
            );

        Assert.assertTrue(
            settingsScreen.isSearchFieldVisible()
        );

        settingsScreen.search("Wi-Fi");
    }

    @Test
    public void verifyGeneralNavigation() {
        IOSSettingsScreen settingsScreen =
                new IOSSettingsScreen(DriverManager.getDriver());

        Assert.assertTrue(
                settingsScreen.isGeneralVisible(),
                "General option should be visible"
        );

        settingsScreen.tapGeneral();
    }

    @Test
    public void verifyGeneralAboutNavigation() {

        IOSSettingsScreen settingsScreen =
            new IOSSettingsScreen(DriverManager.getDriver());

        Assert.assertTrue(
            settingsScreen.isGeneralVisible(),
            "General option should be visible"
        );

        settingsScreen.tapGeneral();

        IOSGeneralScreen generalScreen =
            new IOSGeneralScreen(DriverManager.getDriver());

        Assert.assertTrue(
            generalScreen.isAboutVisible(),
            "About option should be visible"
        );

        generalScreen.tapAbout();

        IOSAboutScreen aboutScreen =
            new IOSAboutScreen(DriverManager.getDriver());

        Assert.assertTrue(
            aboutScreen.isLoaded(),
            "About screen should be visible"
        );
    }
}