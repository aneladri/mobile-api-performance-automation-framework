package web.tests.parallel;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import web.tests.base.BaseWebTest;

import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ParallelExecutionTest extends BaseWebTest {

    private static final int EXPECTED_EXECUTIONS = 4;
    private static final long START_TIMEOUT_SECONDS = 30;

    private static final Set<Long> THREAD_IDS =
            ConcurrentHashMap.newKeySet();

    private static final Set<Integer> MANAGER_IDENTITIES =
            ConcurrentHashMap.newKeySet();

    private static final Set<Integer> BROWSER_IDENTITIES =
            ConcurrentHashMap.newKeySet();

    private static final Set<Integer> CONTEXT_IDENTITIES =
            ConcurrentHashMap.newKeySet();

    private static final Set<Integer> PAGE_IDENTITIES =
            ConcurrentHashMap.newKeySet();

    private static final Set<Path> ARTIFACT_DIRECTORIES =
            ConcurrentHashMap.newKeySet();

    private static CountDownLatch readyLatch;
    private static CountDownLatch releaseLatch;

    @BeforeClass(alwaysRun = true)
    public void initializeParallelValidation() {
        THREAD_IDS.clear();
        MANAGER_IDENTITIES.clear();
        BROWSER_IDENTITIES.clear();
        CONTEXT_IDENTITIES.clear();
        PAGE_IDENTITIES.clear();
        ARTIFACT_DIRECTORIES.clear();

        readyLatch = new CountDownLatch(EXPECTED_EXECUTIONS);
        releaseLatch = new CountDownLatch(1);
    }

    @Test
    public void parallelExecutionOne() throws Exception {
        executeScenario("Parallel Scenario One");
    }

    @Test
    public void parallelExecutionTwo() throws Exception {
        executeScenario("Parallel Scenario Two");
    }

    @Test
    public void parallelExecutionThree() throws Exception {
        executeScenario("Parallel Scenario Three");
    }

    @Test
    public void parallelExecutionFour() throws Exception {
        executeScenario("Parallel Scenario Four");
    }

    @AfterClass(alwaysRun = true)
    public void verifyParallelIsolation() {
        Assert.assertEquals(
                THREAD_IDS.size(),
                EXPECTED_EXECUTIONS,
                "Each scenario should execute on a distinct TestNG thread"
        );

        Assert.assertEquals(
                MANAGER_IDENTITIES.size(),
                EXPECTED_EXECUTIONS,
                "Each scenario should have a unique PlaywrightManager"
        );

        Assert.assertEquals(
                BROWSER_IDENTITIES.size(),
                EXPECTED_EXECUTIONS,
                "Each scenario should have a unique Browser"
        );

        Assert.assertEquals(
                CONTEXT_IDENTITIES.size(),
                EXPECTED_EXECUTIONS,
                "Each scenario should have a unique BrowserContext"
        );

        Assert.assertEquals(
                PAGE_IDENTITIES.size(),
                EXPECTED_EXECUTIONS,
                "Each scenario should have a unique Page"
        );

        Assert.assertEquals(
                ARTIFACT_DIRECTORIES.size(),
                EXPECTED_EXECUTIONS,
                "Each scenario should have a unique artifact directory"
        );
    }

    private void executeScenario(
            String scenarioName
    ) throws Exception {

        readyLatch.countDown();

        boolean allExecutionsReady = readyLatch.await(
                START_TIMEOUT_SECONDS,
                TimeUnit.SECONDS
        );

        Assert.assertTrue(
                allExecutionsReady,
                "All parallel scenarios did not start within "
                        + START_TIMEOUT_SECONDS
                        + " seconds"
        );

        releaseLatch.countDown();

        Assert.assertTrue(
                releaseLatch.await(
                        START_TIMEOUT_SECONDS,
                        TimeUnit.SECONDS
                ),
                "Parallel scenarios were not released"
        );

        captureExecutionIdentity();

        long threadId = Thread.currentThread().getId();

        page().setContent(
                """
                <!DOCTYPE html>
                <html>
                  <head>
                    <title>MAPAF Parallel Execution</title>
                    <style>
                      body {
                        font-family: Arial, sans-serif;
                        padding: 40px;
                        background: #f5f5f5;
                      }

                      .card {
                        background: white;
                        border: 1px solid #dddddd;
                        border-radius: 12px;
                        padding: 32px;
                        max-width: 620px;
                        box-shadow: 0 6px 18px rgba(0,0,0,0.08);
                      }

                      h1 {
                        margin-top: 0;
                      }

                      .label {
                        color: #666666;
                        margin-top: 18px;
                      }

                      .value {
                        font-size: 24px;
                        font-weight: bold;
                      }
                    </style>
                  </head>
                  <body>
                    <div class="card">
                      <h1>MAPAF Parallel Execution</h1>

                      <div class="label">Scenario</div>
                      <div id="scenario-name" class="value"></div>

                      <div class="label">Thread ID</div>
                      <div id="thread-id" class="value"></div>

                      <div class="label">Browser</div>
                      <div id="browser-name" class="value">Chromium</div>

                      <div class="label">Execution Mode</div>
                      <div id="execution-mode" class="value">Parallel</div>

                      <div class="label">Configured Threads</div>
                      <div id="thread-count" class="value"></div>

                      <div class="label">Status</div>
                      <div id="status" class="value">Running</div>
                    </div>
                  </body>
                </html>
                """
        );

        setText("#scenario-name", scenarioName);
        setText("#thread-id", Long.toString(threadId));
        setText(
                "#thread-count",
                Integer.toString(configuredThreadCount())
        );

        Assert.assertEquals(
                page().locator("#scenario-name").innerText(),
                scenarioName
        );

        Assert.assertEquals(
                page().locator("#thread-id").innerText(),
                Long.toString(threadId)
        );

        Assert.assertTrue(
                isParallelExecution(),
                "Parallel execution system property should be enabled"
        );

        Assert.assertFalse(
                page().isClosed(),
                "The scenario page should remain open during execution"
        );

        setText("#status", "Passed");

        if (!isHeadless()) {
            page().waitForTimeout(10000);
        }
    }

    private void captureExecutionIdentity() {
        THREAD_IDS.add(
                Thread.currentThread().getId()
        );

        MANAGER_IDENTITIES.add(
                System.identityHashCode(manager())
        );

        BROWSER_IDENTITIES.add(
                System.identityHashCode(browser())
        );

        CONTEXT_IDENTITIES.add(
                System.identityHashCode(context())
        );

        PAGE_IDENTITIES.add(
                System.identityHashCode(page())
        );

        ARTIFACT_DIRECTORIES.add(
                artifactDirectory()
                        .toAbsolutePath()
                        .normalize()
        );
    }

    private void setText(
            String selector,
            String value
    ) {
        page()
                .locator(selector)
                .evaluate(
                        "(element, text) => "
                                + "element.textContent = text",
                        value
                );
    }
}