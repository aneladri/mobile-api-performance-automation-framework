package web.tests;

import com.microsoft.playwright.BrowserContext;
import org.testng.Assert;
import org.testng.annotations.Test;
import web.tests.base.BaseWebTest;

public class BaseWebTestIsolationTest
        extends BaseWebTest {

    private BrowserContext previousContext;

    @Test
    public void shouldCreateFreshContextForFirstTest() {
        Assert.assertNotNull(context());

        previousContext = context();
    }

    @Test(
            dependsOnMethods =
                    "shouldCreateFreshContextForFirstTest"
    )
    public void shouldCreateFreshContextForNextTest() {
        Assert.assertNotNull(context());

        Assert.assertNotSame(
                context(),
                previousContext
        );
    }
}