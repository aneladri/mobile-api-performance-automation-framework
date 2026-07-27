package core.base;

import api.mock.ApiMockServer;
import core.config.ConfigManager;
import core.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

public class BaseApiTest extends BaseTest {

    private static final Logger logger = LoggerUtil.getLogger(BaseApiTest.class);
    private static final String USE_LOCAL_MOCK_PROPERTY = "useLocalApiMock";

    protected String baseUrl;

    @BeforeSuite(alwaysRun = true)
    public void startLocalApiMock() {
        if (!useLocalMock()) {
            logger.info("Local API mock is disabled; configured baseUrl will be used");
            return;
        }

        ApiMockServer.start();
        System.setProperty("baseUrl", ApiMockServer.getBaseUrl());
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpApi() {
        baseUrl = ConfigManager.getRequired("baseUrl");
        logger.info("API Base URL: {}", baseUrl);
    }

    @AfterSuite(alwaysRun = true)
    public void stopLocalApiMock() {
        if (!useLocalMock()) {
            return;
        }

        ApiMockServer.stop();
        System.clearProperty("baseUrl");
    }

    private boolean useLocalMock() {
        return Boolean.parseBoolean(ConfigManager.get(USE_LOCAL_MOCK_PROPERTY));
    }
}
