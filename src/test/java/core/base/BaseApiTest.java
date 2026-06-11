package core.base;

import core.config.ConfigManager;
import core.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;

public class BaseApiTest extends BaseTest {

    private static final Logger logger = LoggerUtil.getLogger(BaseApiTest.class);

    protected String baseUrl;

    @BeforeMethod
    public void setUpApi() {
        baseUrl = ConfigManager.getRequired("baseUrl");
        logger.info("API Base URL: {}", baseUrl);
    }
}