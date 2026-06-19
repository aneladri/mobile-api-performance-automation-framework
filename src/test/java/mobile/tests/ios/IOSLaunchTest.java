package mobile.tests.ios;

import core.base.BaseMobileTest;
import core.driver.DriverManager;
import org.testng.Assert;
import org.testng.annotations.Test;

public class IOSLaunchTest extends BaseMobileTest {

    @Test
    public void verifyIOSFrameworkLaunches() {
        Assert.assertNotNull(
                DriverManager.getDriver(),
                "iOS driver should be initialized"
        );
    }
}
