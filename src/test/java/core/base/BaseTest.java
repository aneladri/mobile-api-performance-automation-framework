package core.base;

import core.config.ConfigManager;
import core.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

public class BaseTest {

    private static final Logger logger = LoggerUtil.getLogger(BaseTest.class);

    @BeforeSuite
    public void beforeSuite() {
        logger.info("===== Test Execution Started =====");
        logger.info("Environment: {}", ConfigManager.getEnvironment());
    }

    @AfterSuite
    public void afterSuite() {
        logger.info("===== Test Execution Completed =====");
    }
}