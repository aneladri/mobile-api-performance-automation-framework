package web.tests.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import web.artifacts.ExecutionArtifactManager;
import web.config.WebConfiguration;
import web.driver.PlaywrightManager;

import java.lang.reflect.Method;
import java.nio.file.Path;

public abstract class BaseWebTest {

    private final ThreadLocal<PlaywrightManager> managerHolder = new ThreadLocal<>();

    private final ThreadLocal<ExecutionArtifactManager> artifactManagerHolder = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setUpWebTest(Method testMethod) {

        PlaywrightManager manager = createPlaywrightManager();

        WebConfiguration configuration = createWebConfiguration();

        try {

            manager.initialize(configuration);

            managerHolder.set(manager);

            ExecutionArtifactManager artifactManager = new ExecutionArtifactManager(
                    configuration);

            artifactManager.start(
                    manager.getContext(),
                    manager.getPage(),
                    testMethod == null
                            ? "web-test"
                            : testMethod.getName());

            artifactManagerHolder.set(
                    artifactManager);

        } catch (RuntimeException exception) {

            manager.close();

            managerHolder.remove();
            artifactManagerHolder.remove();

            throw exception;
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownWebTest(
            ITestResult result) {

        PlaywrightManager manager = managerHolder.get();

        ExecutionArtifactManager artifactManager = artifactManagerHolder.get();

        boolean failed = result != null
                && !result.isSuccess();

        try {

            if (artifactManager != null
                    && manager != null
                    && manager.isInitialized()) {

                if (failed) {
                    artifactManager.captureFailureScreenshot(
                            manager.getPage());
                }

                artifactManager.finish(
                        manager.getContext(),
                        failed);
            }

        } finally {

            try {

                if (manager != null) {
                    manager.close();
                }

            } finally {

                artifactManagerHolder.remove();
                managerHolder.remove();
            }
        }
    }

    protected WebConfiguration createWebConfiguration() {

        WebConfiguration configuration = WebConfiguration.defaultConfiguration();

        configuration.setHeadless(
                Boolean.parseBoolean(
                        System.getProperty(
                                "web.headless",
                                "true")));

        String browserProperty = System.getProperty(
                "web.browser",
                "CHROMIUM");

        configuration.setBrowser(
                web.enums.BrowserType.valueOf(
                        browserProperty
                                .trim()
                                .toUpperCase()));

        return configuration;
    }

    protected PlaywrightManager createPlaywrightManager() {
        return new PlaywrightManager();
    }

    protected PlaywrightManager manager() {

        PlaywrightManager manager = managerHolder.get();

        if (manager == null) {
            throw new IllegalStateException(
                    "Playwright manager is not initialized for the current thread");
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

    protected ExecutionArtifactManager artifacts() {

        ExecutionArtifactManager manager = artifactManagerHolder.get();

        if (manager == null) {
            throw new IllegalStateException(
                    "ExecutionArtifactManager is not initialized for the current thread");
        }

        return manager;
    }

    protected Path artifactDirectory() {
        return artifacts().getExecutionDirectory();
    }

    protected boolean isParallelExecution() {
        return Boolean.parseBoolean(
                System.getProperty(
                        "web.parallel",
                        "false"));
    }

    protected int configuredThreadCount() {

        return Integer.parseInt(
                System.getProperty(
                        "web.threads",
                        "1"));
    }

    protected boolean isHeadless() {

        return Boolean.parseBoolean(
                System.getProperty(
                        "web.headless",
                        "true"));
    }
}