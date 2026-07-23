package web.tests.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import web.config.WebConfiguration;
import web.driver.PlaywrightManager;

import org.testng.ITestResult;
import web.artifacts.ExecutionArtifactManager;

import java.nio.file.Path;

public abstract class BaseWebTest {

    private final ThreadLocal<PlaywrightManager> managerHolder = new ThreadLocal<>();

    private final ThreadLocal<ExecutionArtifactManager> artifactManagerHolder = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setUpWebTest(
            java.lang.reflect.Method testMethod) {
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
            ITestResult testResult) {
        PlaywrightManager manager = managerHolder.get();

        ExecutionArtifactManager artifactManager = artifactManagerHolder.get();

        boolean failed = testResult != null
                && !testResult.isSuccess();

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
        return WebConfiguration.defaultConfiguration();
    }

    protected PlaywrightManager createPlaywrightManager() {
        return new PlaywrightManager();
    }

    protected PlaywrightManager manager() {
        PlaywrightManager manager = managerHolder.get();

        if (manager == null) {
            throw new IllegalStateException(
                    "Web test lifecycle is not initialized "
                            + "for the current thread");
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
        ExecutionArtifactManager artifactManager = artifactManagerHolder.get();

        if (artifactManager == null) {
            throw new IllegalStateException(
                    "Execution artifact manager is not initialized "
                            + "for the current thread");
        }

        return artifactManager;
    }

    protected Path artifactDirectory() {
        return artifacts().getExecutionDirectory();
    }
}
