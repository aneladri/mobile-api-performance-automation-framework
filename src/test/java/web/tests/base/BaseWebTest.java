package web.tests.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import web.config.WebConfiguration;
import web.driver.PlaywrightManager;

public abstract class BaseWebTest {

    private final ThreadLocal<PlaywrightManager> managerHolder =
            new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setUpWebTest() {
        PlaywrightManager manager =
                createPlaywrightManager();

        WebConfiguration configuration =
                createWebConfiguration();

        try {
            manager.initialize(configuration);
            managerHolder.set(manager);
        } catch (RuntimeException exception) {
            manager.close();
            managerHolder.remove();

            throw exception;
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownWebTest() {
        PlaywrightManager manager =
                managerHolder.get();

        try {
            if (manager != null) {
                manager.close();
            }
        } finally {
            managerHolder.remove();
        }
    }

    protected WebConfiguration createWebConfiguration() {
        return WebConfiguration.defaultConfiguration();
    }

    protected PlaywrightManager createPlaywrightManager() {
        return new PlaywrightManager();
    }

    protected PlaywrightManager manager() {
        PlaywrightManager manager =
                managerHolder.get();

        if (manager == null) {
            throw new IllegalStateException(
                    "Web test lifecycle is not initialized "
                            + "for the current thread"
            );
        }

        return manager;
    }

    protected Playwright playwright() {
        return manager().getPlaywright();
    }

    protected Browser browser() {
        return manager().getBrowser();
    }

    protected BrowserContext context() {
        return manager().getContext();
    }

    protected Page page() {
        return manager().getPage();
    }

    protected Page newPage() {
        return manager().newPage();
    }
}
