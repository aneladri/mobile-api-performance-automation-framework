package web.driver;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;
import web.config.WebConfiguration;
import web.enums.ExecutionMode;

public class BrowserFactory {

    public Browser create(
            Playwright playwright,
            WebConfiguration configuration
    ) {
        if (playwright == null) {
            throw new IllegalArgumentException(
                    "Playwright must not be null"
            );
        }

        if (configuration == null) {
            throw new IllegalArgumentException(
                    "Web configuration must not be null"
            );
        }

        if (configuration.getExecutionMode()
                == ExecutionMode.REMOTE) {

            throw new UnsupportedOperationException(
                    "Remote browser execution is not supported yet"
            );
        }

        com.microsoft.playwright.BrowserType browserType =
                resolveBrowserType(
                        playwright,
                        configuration.getBrowser()
                );

        return browserType.launch(
                new com.microsoft.playwright.BrowserType
                        .LaunchOptions()
                        .setHeadless(
                                configuration.isHeadless()
                        )
        );
    }

    private com.microsoft.playwright.BrowserType
    resolveBrowserType(
            Playwright playwright,
            web.enums.BrowserType browserType
    ) {
        if (browserType == null) {
            throw new IllegalArgumentException(
                    "Browser type must not be null"
            );
        }

        return switch (browserType) {
            case CHROMIUM -> playwright.chromium();
            case FIREFOX -> playwright.firefox();
            case WEBKIT -> playwright.webkit();
        };
    }
}
