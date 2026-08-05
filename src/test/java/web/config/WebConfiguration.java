package web.config;

import web.enums.BrowserType;
import web.enums.ExecutionMode;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WebConfiguration {

    private BrowserType browser = BrowserType.CHROMIUM;

    private ExecutionMode executionMode = ExecutionMode.LOCAL;

    private boolean headless = true;

    private int viewportWidth = 1440;

    private int viewportHeight = 900;

    private int timeoutSeconds = 30;

    private boolean screenshotsEnabled = true;

    private boolean traceEnabled = true;

    private boolean saveTraceOnSuccess = false;

    private boolean videoEnabled = true;

    private boolean consoleLogsEnabled = true;

    private Path artifactDirectory = Paths.get("web", "artifacts");

    public boolean isScreenshotsEnabled() {
        return screenshotsEnabled;
    }

    public void setScreenshotsEnabled(
            boolean screenshotsEnabled) {
        this.screenshotsEnabled = screenshotsEnabled;
    }

    public boolean isTraceEnabled() {
        return traceEnabled;
    }

    public void setTraceEnabled(
            boolean traceEnabled) {
        this.traceEnabled = traceEnabled;
    }

    public boolean isSaveTraceOnSuccess() {
        return saveTraceOnSuccess;
    }

    public void setSaveTraceOnSuccess(boolean saveTraceOnSuccess) {
        this.saveTraceOnSuccess = saveTraceOnSuccess;
    }

    public boolean isVideoEnabled() {
        return videoEnabled;
    }

    public void setVideoEnabled(
            boolean videoEnabled) {
        this.videoEnabled = videoEnabled;
    }

    public boolean isConsoleLogsEnabled() {
        return consoleLogsEnabled;
    }

    public void setConsoleLogsEnabled(
            boolean consoleLogsEnabled) {
        this.consoleLogsEnabled = consoleLogsEnabled;
    }

    public Path getArtifactDirectory() {
        return artifactDirectory;
    }

    public void setArtifactDirectory(
            Path artifactDirectory) {
        if (artifactDirectory == null) {
            throw new IllegalArgumentException(
                    "Artifact directory must not be null");
        }

        this.artifactDirectory = artifactDirectory;
    }

    public static WebConfiguration defaultConfiguration() {
        return new WebConfiguration();
    }

    public BrowserType getBrowser() {
        return browser;
    }

    public void setBrowser(BrowserType browser) {
        if (browser == null) {
            throw new IllegalArgumentException(
                    "Browser type must not be null");
        }

        this.browser = browser;
    }

    public ExecutionMode getExecutionMode() {
        return executionMode;
    }

    public void setExecutionMode(
            ExecutionMode executionMode) {
        if (executionMode == null) {
            throw new IllegalArgumentException(
                    "Execution mode must not be null");
        }

        this.executionMode = executionMode;
    }

    public boolean isHeadless() {
        return headless;
    }

    public void setHeadless(boolean headless) {
        this.headless = headless;
    }

    public int getViewportWidth() {
        return viewportWidth;
    }

    public void setViewportWidth(int viewportWidth) {

        if (viewportWidth <= 0) {
            throw new IllegalArgumentException(
                    "Viewport width must be greater than zero");
        }

        this.viewportWidth = viewportWidth;
    }

    public int getViewportHeight() {
        return viewportHeight;
    }

    public void setViewportHeight(int viewportHeight) {

        if (viewportHeight <= 0) {
            throw new IllegalArgumentException(
                    "Viewport height must be greater than zero");
        }

        this.viewportHeight = viewportHeight;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(
            int timeoutSeconds) {

        if (timeoutSeconds <= 0) {
            throw new IllegalArgumentException(
                    "Timeout must be greater than zero");
        }

        this.timeoutSeconds = timeoutSeconds;
    }
}
