package core.base;

import core.config.ConfigManager;
import core.driver.DriverFactory;
import core.driver.DriverManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

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
}