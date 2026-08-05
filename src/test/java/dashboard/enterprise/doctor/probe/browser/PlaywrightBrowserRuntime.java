package dashboard.enterprise.doctor.probe.browser;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class PlaywrightBrowserRuntime implements BrowserRuntime {

    @Override
    public BrowserRuntimeResult inspect(
            Path repositoryRoot,
            Path evidenceDirectory
    ) {
        long started = System.nanoTime();

        boolean playwrightAvailable = false;
        boolean browserLaunched = false;
        boolean pageRendered = false;
        boolean screenshotCreated = false;
        boolean traceCreated = false;

        String browserVersion = "";
        String diagnosis = "Browser runtime is operational.";

        List<String> evidence = new ArrayList<>();

        Path screenshot = evidenceDirectory.resolve(
                "browser-health-screenshot.png"
        );

        Path trace = evidenceDirectory.resolve(
                "browser-health-trace.zip"
        );

        try {
            Files.createDirectories(evidenceDirectory);

            Class.forName("com.microsoft.playwright.Playwright");
            playwrightAvailable = true;

            try (Playwright playwright = Playwright.create()) {
                Browser browser = playwright.chromium().launch(
                        new BrowserType.LaunchOptions()
                                .setHeadless(true)
                );

                try {
                    browserLaunched = true;
                    browserVersion = browser.version();

                    BrowserContext context =
                            browser.newContext();

                    try {
                        context.tracing().start(
                                new Tracing.StartOptions()
                                        .setScreenshots(true)
                                        .setSnapshots(true)
                                        .setSources(true)
                        );

                        Page page = context.newPage();

                        page.setContent("""
                                <!doctype html>
                                <html lang="en">
                                  <head>
                                    <title>MAPAF Browser Health</title>
                                  </head>
                                  <body>
                                    <main id="mapaf-browser-health">
                                      MAPAF Browser Health Ready
                                    </main>
                                  </body>
                                </html>
                                """);

                        String title = page.title();
                        String content = page.locator(
                                "#mapaf-browser-health"
                        ).textContent();

                        pageRendered =
                                "MAPAF Browser Health".equals(title)
                                        && content != null
                                        && content.contains(
                                        "Browser Health Ready"
                                );

                        page.screenshot(
                                new Page.ScreenshotOptions()
                                        .setPath(screenshot)
                                        .setFullPage(true)
                        );

                        screenshotCreated =
                                Files.isRegularFile(screenshot)
                                        && Files.size(screenshot) > 0;

                        if (screenshotCreated) {
                            evidence.add(
                                    repositoryRoot.relativize(
                                            screenshot
                                    ).toString()
                            );
                        }

                        context.tracing().stop(
                                new Tracing.StopOptions()
                                        .setPath(trace)
                        );

                        traceCreated =
                                Files.isRegularFile(trace)
                                        && Files.size(trace) > 0;

                        if (traceCreated) {
                            evidence.add(
                                    repositoryRoot.relativize(
                                            trace
                                    ).toString()
                            );
                        }

                    } finally {
                        context.close();
                    }

                } finally {
                    browser.close();
                }
            }

        } catch (ClassNotFoundException exception) {
            diagnosis =
                    "Playwright Java runtime is not available on the classpath.";

        } catch (Exception exception) {
            diagnosis =
                    exception.getClass().getSimpleName()
                            + ": "
                            + safeMessage(exception);
        }

        return new BrowserRuntimeResult(
                playwrightAvailable,
                browserLaunched,
                pageRendered,
                screenshotCreated,
                traceCreated,
                browserVersion,
                diagnosis,
                elapsedMillis(started),
                evidence
        );
    }

    private long elapsedMillis(long started) {
        return Math.max(
                0,
                (System.nanoTime() - started) / 1_000_000
        );
    }

    private String safeMessage(Exception exception) {
        return exception.getMessage() == null
                ? "No exception message was provided."
                : exception.getMessage();
    }
}
