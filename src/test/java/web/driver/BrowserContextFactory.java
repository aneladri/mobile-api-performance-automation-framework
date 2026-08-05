package web.driver;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import web.config.WebConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BrowserContextFactory {

    public BrowserContext create(
            Browser browser,
            WebConfiguration configuration
    ) {
        if (browser == null) {
            throw new IllegalArgumentException(
                    "Browser must not be null"
            );
        }

        if (configuration == null) {
            throw new IllegalArgumentException(
                    "Web configuration must not be null"
            );
        }

        Browser.NewContextOptions contextOptions =
                new Browser.NewContextOptions()
                        .setViewportSize(
                                configuration.getViewportWidth(),
                                configuration.getViewportHeight()
                        );

        if (configuration.isVideoEnabled()) {
            Path videoDirectory =
                    configuration
                            .getArtifactDirectory()
                            .resolve("videos");

            try {
                Files.createDirectories(videoDirectory);
            } catch (IOException exception) {
                throw new IllegalStateException(
                        "Unable to create video artifact directory: "
                                + videoDirectory.toAbsolutePath(),
                        exception
                );
            }

            contextOptions.setRecordVideoDir(videoDirectory);
        }

        BrowserContext context =
                browser.newContext(contextOptions);

        double timeoutMilliseconds =
                configuration.getTimeoutSeconds() * 1000.0;

        context.setDefaultTimeout(timeoutMilliseconds);
        context.setDefaultNavigationTimeout(
                timeoutMilliseconds
        );

        return context;
    }
}