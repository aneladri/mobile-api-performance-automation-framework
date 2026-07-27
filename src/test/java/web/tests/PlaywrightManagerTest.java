package web.tests;

import com.microsoft.playwright.Page;
import org.testng.Assert;
import org.testng.annotations.Test;
import web.config.WebConfiguration;
import web.driver.PlaywrightManager;
import web.enums.BrowserType;

public class PlaywrightManagerTest {

    @Test
    public void shouldInitializePlaywrightResources() {
        WebConfiguration configuration =
                WebConfiguration.defaultConfiguration();

        configuration.setBrowser(
                BrowserType.CHROMIUM
        );

        configuration.setHeadless(true);

        try (PlaywrightManager manager =
                     new PlaywrightManager()) {

            manager.initialize(configuration);

            Assert.assertTrue(
                    manager.isInitialized()
            );

            Assert.assertNotNull(
                    manager.getPlaywright()
            );

            Assert.assertNotNull(
                    manager.getBrowser()
            );

            Assert.assertNotNull(
                    manager.getContext()
            );

            Assert.assertNotNull(
                    manager.getPage()
            );
        }
    }

    @Test
    public void shouldNavigateUsingManagedPage() {
        WebConfiguration configuration =
                WebConfiguration.defaultConfiguration();

        try (PlaywrightManager manager =
                     new PlaywrightManager()) {

            manager.initialize(configuration);

            Page page = manager.getPage();

            page.setContent(
                    """
                    <!DOCTYPE html>
                    <html>
                      <head>
                        <title>MAPAF Web Test</title>
                      </head>
                      <body>
                        <h1>MAPAF Playwright Engine</h1>
                      </body>
                    </html>
                    """
            );

            Assert.assertEquals(
                    page.title(),
                    "MAPAF Web Test"
            );

            Assert.assertEquals(
                    page.locator("h1").textContent(),
                    "MAPAF Playwright Engine"
            );
        }
    }

    @Test
    public void shouldCreateAdditionalPage() {
        try (PlaywrightManager manager =
                     new PlaywrightManager()) {

            manager.initialize(
                    WebConfiguration.defaultConfiguration()
            );

            Page originalPage =
                    manager.getPage();

            Page additionalPage =
                    manager.newPage();

            Assert.assertNotSame(
                    additionalPage,
                    originalPage
            );

            Assert.assertEquals(
                    manager.getPage(),
                    additionalPage
            );

            Assert.assertEquals(
                    manager.getContext().pages().size(),
                    2
            );
        }
    }

    @Test
    public void shouldCloseResourcesSafely() {
        PlaywrightManager manager =
                new PlaywrightManager();

        manager.initialize(
                WebConfiguration.defaultConfiguration()
        );

        Assert.assertTrue(
                manager.isInitialized()
        );

        manager.close();

        Assert.assertFalse(
                manager.isInitialized()
        );

        manager.close();

        Assert.assertFalse(
                manager.isInitialized()
        );
    }

    @Test(
            expectedExceptions = IllegalStateException.class,
            expectedExceptionsMessageRegExp =
                    "Playwright manager is not initialized "
                            + "for the current thread"
    )
    public void shouldRejectResourceAccessBeforeInitialization() {
        new PlaywrightManager().getPage();
    }

    @Test(
            expectedExceptions = IllegalStateException.class,
            expectedExceptionsMessageRegExp =
                    "Playwright manager is already initialized "
                            + "for the current thread"
    )
    public void shouldRejectDuplicateInitialization() {
        try (PlaywrightManager manager =
                     new PlaywrightManager()) {

            WebConfiguration configuration =
                    WebConfiguration.defaultConfiguration();

            manager.initialize(configuration);
            manager.initialize(configuration);
        }
    }

    @Test(
            expectedExceptions =
                    UnsupportedOperationException.class,
            expectedExceptionsMessageRegExp =
                    "Remote browser execution is not supported yet"
    )
    public void shouldRejectUnsupportedRemoteExecution() {
        WebConfiguration configuration =
                WebConfiguration.defaultConfiguration();

        configuration.setExecutionMode(
                web.enums.ExecutionMode.REMOTE
        );

        try (PlaywrightManager manager =
                     new PlaywrightManager()) {

            manager.initialize(configuration);
        }
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Web configuration must not be null"
    )
    public void shouldRejectNullConfiguration() {
        new PlaywrightManager().initialize(null);
    }
}
