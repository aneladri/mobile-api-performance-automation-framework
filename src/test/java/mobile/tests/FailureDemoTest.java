package mobile.tests;

import core.base.BaseMobileTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FailureDemoTest extends BaseMobileTest {

    @Test
    public void verifyFailureScreenshotWorks() {

        Assert.fail(
                "Intentional failure"
        );
    }
}
