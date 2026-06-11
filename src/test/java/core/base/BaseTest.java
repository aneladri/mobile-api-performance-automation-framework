package core.base;

import core.config.ConfigManager;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

public class BaseTest {

    @BeforeSuite
    public void beforeSuite() {
        System.out.println("===== Test Execution Started =====");
        System.out.println("Environment: " + ConfigManager.getEnvironment());
    }

    @AfterSuite
    public void afterSuite() {
        System.out.println("===== Test Execution Completed =====");
    }
}