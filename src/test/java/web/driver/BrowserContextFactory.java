package web.driver;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import web.config.WebConfiguration;

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

        BrowserContext context =
                browser.newContext(contextOptions);

        double timeoutMillis =
                configuration.getTimeoutSeconds() * 1000.0;

        context.setDefaultTimeout(timeoutMillis);
        context.setDefaultNavigationTimeout(timeoutMillis);

        return context;
    }
}
