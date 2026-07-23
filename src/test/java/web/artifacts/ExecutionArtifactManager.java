package web.artifacts;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.ConsoleMessage;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import web.config.WebConfiguration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ExecutionArtifactManager {

        private final WebConfiguration configuration;

        private final List<String> consoleEntries = new ArrayList<>();

        private final List<String> pageErrors = new ArrayList<>();

        private Path executionDirectory;
        private boolean tracingStarted;

        public ExecutionArtifactManager(
                        WebConfiguration configuration) {
                if (configuration == null) {
                        throw new IllegalArgumentException(
                                        "Web configuration must not be null");
                }

                this.configuration = configuration;
        }

        public Path start(
                        BrowserContext context,
                        Page page,
                        String executionName) {
                validateContextAndPage(context, page);

                executionDirectory = createExecutionDirectory(
                                executionName);

                if (configuration.isConsoleLogsEnabled()) {
                        registerPageListeners(page);
                }

                if (configuration.isTraceEnabled()) {
                        context.tracing().start(
                                        new Tracing.StartOptions()
                                                        .setScreenshots(true)
                                                        .setSnapshots(true)
                                                        .setSources(true));

                        tracingStarted = true;
                }

                return executionDirectory;
        }

        public Path captureFailureScreenshot(
                        Page page) {
                if (!configuration.isScreenshotsEnabled()
                                || page == null
                                || page.isClosed()
                                || executionDirectory == null) {

                        return null;
                }

                Path screenshot = executionDirectory.resolve(
                                "failure-screenshot.png");

                page.screenshot(
                                new Page.ScreenshotOptions()
                                                .setPath(screenshot)
                                                .setFullPage(true));

                return screenshot.toAbsolutePath();
        }

        public Path finish(
                        BrowserContext context,
                        boolean failed) {
                if (executionDirectory == null) {
                        return null;
                }

                stopTracing(context, failed);
                writeLogs();

                return executionDirectory.toAbsolutePath();
        }

        public Path getExecutionDirectory() {
                return executionDirectory;
        }

        public List<String> getConsoleEntries() {
                return new ArrayList<>(consoleEntries);
        }

        public List<String> getPageErrors() {
                return new ArrayList<>(pageErrors);
        }

        private void registerPageListeners(
                        Page page) {
                page.onConsoleMessage(
                                message -> consoleEntries.add(
                                                formatConsoleMessage(message)));

                page.onPageError(
                                error -> pageErrors.add(
                                                Instant.now()
                                                                + " | PAGE_ERROR | "
                                                                + error));
        }

        private String formatConsoleMessage(
                        ConsoleMessage message) {
                return Instant.now()
                                + " | "
                                + message.type()
                                + " | "
                                + message.text();
        }

        private void stopTracing(
                        BrowserContext context,
                        boolean failed) {
                if (!tracingStarted || context == null) {
                        return;
                }

                try {
                        if (failed) {
                                context.tracing().stop(
                                                new Tracing.StopOptions()
                                                                .setPath(
                                                                                executionDirectory.resolve(
                                                                                                "trace.zip")));
                        } else {
                                context.tracing().stop();
                        }
                } finally {
                        tracingStarted = false;
                }
        }

        private void writeLogs() {
                if (!configuration.isConsoleLogsEnabled()) {
                        return;
                }

                writeLines(
                                executionDirectory.resolve(
                                                "console.log"),
                                consoleEntries);

                writeLines(
                                executionDirectory.resolve(
                                                "page-errors.log"),
                                pageErrors);
        }

        private void writeLines(
                        Path outputFile,
                        List<String> lines) {
                try {
                        Files.write(
                                        outputFile,
                                        lines,
                                        StandardCharsets.UTF_8);
                } catch (IOException exception) {
                        throw new IllegalStateException(
                                        "Unable to write artifact log: "
                                                        + outputFile.toAbsolutePath(),
                                        exception);
                }
        }

        private Path createExecutionDirectory(
                        String executionName) {
                String safeName = sanitizeExecutionName(
                                executionName);

                String timestamp = Long.toString(
                                System.currentTimeMillis());

                Path directory = configuration
                                .getArtifactDirectory()
                                .resolve(
                                                safeName + "-" + timestamp);

                try {
                        Files.createDirectories(directory);
                } catch (IOException exception) {
                        throw new IllegalStateException(
                                        "Unable to create execution artifact directory: "
                                                        + directory.toAbsolutePath(),
                                        exception);
                }

                return directory;
        }

        private String sanitizeExecutionName(
                        String executionName) {
                if (executionName == null
                                || executionName.isBlank()) {

                        return "web-execution";
                }

                return executionName
                                .trim()
                                .replaceAll(
                                                "[^a-zA-Z0-9._-]",
                                                "-");
        }

        private void validateContextAndPage(
                        BrowserContext context,
                        Page page) {
                if (context == null) {
                        throw new IllegalArgumentException(
                                        "Browser context must not be null");
                }

                if (page == null) {
                        throw new IllegalArgumentException(
                                        "Playwright page must not be null");
                }
        }
}
