package web.tests;

import com.microsoft.playwright.Page;
import org.testng.Assert;
import org.testng.annotations.Test;
import web.tests.base.BaseWebTest;

public class BaseWebTestLifecycleTest
        extends BaseWebTest {

    @Test
    public void shouldInitializeWebResourcesBeforeTest() {
        Assert.assertNotNull(
                playwright()
        );

        Assert.assertNotNull(
                browser()
        );

        Assert.assertNotNull(
                context()
        );

        Assert.assertNotNull(
                page()
        );

        Assert.assertTrue(
                manager().isInitialized()
        );
    }

    @Test
    public void shouldUseManagedPage() {
        page().setContent(
                """
                <!DOCTYPE html>
                <html>
                  <head>
                    <title>MAPAF Base Web Test</title>
                  </head>
                  <body>
                    <main>
                      <h1>Base Web Test Lifecycle</h1>
                    </main>
                  </body>
                </html>
                """
        );

        Assert.assertEquals(
                page().title(),
                "MAPAF Base Web Test"
        );

        Assert.assertEquals(
                page()
                        .locator("h1")
                        .textContent(),
                "Base Web Test Lifecycle"
        );
    }

    @Test
    public void shouldCreateAdditionalPage() {
        Page originalPage = page();

        Page additionalPage = newPage();

        Assert.assertNotSame(
                additionalPage,
                originalPage
        );

        Assert.assertEquals(
                page(),
                additionalPage
        );

        Assert.assertEquals(
                context().pages().size(),
                2
        );
    }
}
