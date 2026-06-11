package core.base;

import core.config.ConfigManager;
import org.testng.annotations.BeforeMethod;

public class BaseApiTest extends BaseTest {

    protected String baseUrl;

    @BeforeMethod
    public void setUpApi() {
        baseUrl = ConfigManager.getRequired("baseUrl");
        System.out.println("API Base URL: " + baseUrl);
    }
}