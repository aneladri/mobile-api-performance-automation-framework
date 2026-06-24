package core.base;

import core.config.ConfigManager;
import core.driver.DriverFactory;
import core.driver.DriverManager;
import core.utils.LoggerUtil;
import mobile.utils.ScreenshotUtils;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseMobileTest extends BaseTest {

    private static final Logger logger =
            LoggerUtil.getLogger(BaseMobileTest.class);

    @BeforeMethod(alwaysRun = true)
    public void setUpMobile() {
        String platform = ConfigManager.getRequired("platform");
        DriverFactory.createDriver(platform);
    }

  @AfterMethod(alwaysRun = true)
    public void captureScreenshotAndTearDown(ITestResult result) {

        try {
            if (DriverManager.getDriver() != null) {

                boolean shouldCapture =
                    result.getStatus() == ITestResult.FAILURE
                            || result.getStatus() == ITestResult.SKIP;

                if (shouldCapture) {
                    ScreenshotUtils.attachScreenshotToAllure();
                }
            }

        } catch (Exception e) {
            logger.warn(
                "Screenshot capture skipped: {}",
                    e.getMessage()
            );

        } finally {
            DriverManager.quitDriver();
        }
    }
}