package mobile.demo;

import core.base.BaseMobileTest;
import core.driver.DriverManager;
import core.reporting.demo.DemoReporter;
import mobile.utils.ScreenshotUtils;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import core.config.ConfigManager;
import mobile.utils.DeviceInfoUtils;

public class MobileFrameworkDemoTest extends BaseMobileTest {

    private static final int TOTAL_STEPS = 4;

    private int screenshotCount;
    private boolean executionPassed;

    @Test(description = "Enterprise Mobile Automation Demonstration")
    public void demonstrateMobileAutomationFramework() {

        DemoReporter.banner(
                "Mobile Inspection Workflow",
                ConfigManager.getEnvironment(),
                DeviceInfoUtils.getPlatformName(),
                DeviceInfoUtils.getPlatformVersion(),
                DeviceInfoUtils.getDeviceName(),
                DeviceInfoUtils.getAutomationName(),
                "Local Emulator",
                "ApiDemos");

        try {
            DemoReporter.step(
                    1,
                    TOTAL_STEPS,
                    "Validating Appium session");

            Assert.assertNotNull(
                    DriverManager.getDriver(),
                    "Appium driver was not initialized");

            DemoReporter.pass();

            DemoReporter.step(
                    2,
                    TOTAL_STEPS,
                    "Validating application home screen");

            ApiDemosMainScreen mainScreen = new ApiDemosMainScreen();

            Assert.assertTrue(
                    mainScreen.isMainScreenDisplayed(),
                    "Application home screen was not displayed");

            DemoReporter.pass();

            captureEvidence();

            DemoReporter.step(
                    3,
                    TOTAL_STEPS,
                    "Opening Inspection module");

            ApiDemosNavigationFlow flow = new ApiDemosNavigationFlow();

            ViewsCategoryScreen viewsScreen = flow.openViewsCategory();

            DemoReporter.pass();

            DemoReporter.step(
                    4,
                    TOTAL_STEPS,
                    "Validating destination screen");

            Assert.assertTrue(
                    viewsScreen.isViewsCategoryScreenDisplayed(),
                    "Views screen was not displayed");

            DemoReporter.pass();

            captureEvidence();

            executionPassed = true;

        } catch (Throwable error) {
            DemoReporter.fail(error.getMessage());
            throw error;
        }
    }

    @AfterMethod(alwaysRun = true)
    public void completeDemo(ITestResult result) {

        captureEvidence();

        DemoReporter.summary(
                result.isSuccess() && executionPassed
                        ? "PASSED"
                        : "FAILED",
                0,
                screenshotCount,
                DeviceInfoUtils.getSessionId());
    }

    private void captureEvidence() {
        try {
            ScreenshotUtils.attachScreenshotToAllure();
            screenshotCount++;

        } catch (Exception exception) {
            System.out.println(
                    "[Demo] Screenshot capture skipped: "
                            + exception.getMessage());
        }
    }
}