package web.driver;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import web.config.WebConfiguration;

public class PlaywrightManager
        implements AutoCloseable {

    private final BrowserFactory browserFactory;
    private final BrowserContextFactory contextFactory;

    private final ThreadLocal<ExecutionState> executionState =
            new ThreadLocal<>();

    public PlaywrightManager() {
        this(
                new BrowserFactory(),
                new BrowserContextFactory()
        );
    }

    PlaywrightManager(
            BrowserFactory browserFactory,
            BrowserContextFactory contextFactory
    ) {
        if (browserFactory == null) {
            throw new IllegalArgumentException(
                    "Browser factory must not be null"
            );
        }

        if (contextFactory == null) {
            throw new IllegalArgumentException(
                    "Browser context factory must not be null"
            );
        }

        this.browserFactory = browserFactory;
        this.contextFactory = contextFactory;
    }

    public void initialize(
            WebConfiguration configuration
    ) {
        if (configuration == null) {
            throw new IllegalArgumentException(
                    "Web configuration must not be null"
            );
        }

        if (isInitialized()) {
            throw new IllegalStateException(
                    "Playwright manager is already initialized "
                            + "for the current thread"
            );
        }

        ExecutionState state =
                new ExecutionState();

        try {
            state.playwright = Playwright.create();

            state.browser = browserFactory.create(
                    state.playwright,
                    configuration
            );

            state.context = contextFactory.create(
                    state.browser,
                    configuration
            );

            state.page = state.context.newPage();

            executionState.set(state);
        } catch (RuntimeException exception) {
            closeState(state);
            executionState.remove();

            throw exception;
        }
    }

    public Page newPage() {
        BrowserContext context = getContext();

        Page page = context.newPage();

        getState().page = page;

        return page;
    }

    public boolean isInitialized() {
        ExecutionState state =
                executionState.get();

        return state != null
                && state.playwright != null
                && state.browser != null
                && state.context != null
                && state.page != null;
    }

    public Playwright getPlaywright() {
        return getState().playwright;
    }

    public Browser getBrowser() {
        return getState().browser;
    }

    public BrowserContext getContext() {
        return getState().context;
    }

    public Page getPage() {
        return getState().page;
    }

    @Override
    public void close() {
        ExecutionState state =
                executionState.get();

        if (state == null) {
            return;
        }

        try {
            closeState(state);
        } finally {
            executionState.remove();
        }
    }

    private ExecutionState getState() {
        ExecutionState state =
                executionState.get();

        if (state == null) {
            throw new IllegalStateException(
                    "Playwright manager is not initialized "
                            + "for the current thread"
            );
        }

        return state;
    }

    private void closeState(
            ExecutionState state
    ) {
        if (state == null) {
            return;
        }

        closeContext(state);
        closeBrowser(state);
        closePlaywright(state);
    }

    private void closeContext(
            ExecutionState state
    ) {
        if (state.context == null) {
            return;
        }

        try {
            state.context.close();
        } catch (RuntimeException ignored) {
            // Continue closing remaining resources.
        } finally {
            state.context = null;
            state.page = null;
        }
    }

    private void closeBrowser(
            ExecutionState state
    ) {
        if (state.browser == null) {
            return;
        }

        try {
            state.browser.close();
        } catch (RuntimeException ignored) {
            // Continue closing remaining resources.
        } finally {
            state.browser = null;
        }
    }

    private void closePlaywright(
            ExecutionState state
    ) {
        if (state.playwright == null) {
            return;
        }

        try {
            state.playwright.close();
        } catch (RuntimeException ignored) {
            // Resource is already being shut down.
        } finally {
            state.playwright = null;
        }
    }

    private static final class ExecutionState {

        private Playwright playwright;
        private Browser browser;
        private BrowserContext context;
        private Page page;
    }
}
