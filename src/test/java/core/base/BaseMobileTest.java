package core.base;

import core.config.ConfigManager;
import core.driver.DriverFactory;
import core.driver.DriverManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import mobile.utils.ScreenshotUtils;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;

public class BaseMobileTest extends BaseTest {

    @BeforeMethod
    public void setUpMobile() {
        String platform = ConfigManager.getRequired("platform");
        DriverFactory.createDriver(platform);
    }

    @AfterMethod
    public void tearDownMobile() {
        DriverManager.quitDriver();
    }

    @AfterMethod
    public void captureFailureScreenshot(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            String screenshotPath = ScreenshotUtils.captureScreenshot(
                result.getMethod().getMethodName()
            );

            logger.error("Screenshot captured: " + screenshotPath);
        }
    }
}