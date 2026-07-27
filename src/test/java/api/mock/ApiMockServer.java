package api.mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import core.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

/**
 * Embedded HTTP server used by the API framework tests.
 *
 * <p>The server starts on a random available port so it can run reliably on
 * developer machines and CI agents without requiring a separately installed
 * service.</p>
 */
public final class ApiMockServer {

    private static final Logger logger = LoggerUtil.getLogger(ApiMockServer.class);
    private static WireMockServer server;

    private ApiMockServer() {
    }

    public static synchronized void start() {
        if (server != null && server.isRunning()) {
            return;
        }

        server = new WireMockServer(options().dynamicPort());
        server.start();
        ApiMockStubs.register(server);

        logger.info("Local API mock server started at {}", getBaseUrl());
    }

    public static synchronized void stop() {
        if (server == null) {
            return;
        }

        if (server.isRunning()) {
            server.stop();
            logger.info("Local API mock server stopped");
        }
        server = null;
    }

    public static boolean isRunning() {
        return server != null && server.isRunning();
    }

    public static String getBaseUrl() {
        if (!isRunning()) {
            throw new IllegalStateException("Local API mock server is not running");
        }
        return server.baseUrl();
    }

    public static WireMockServer instance() {
        if (!isRunning()) {
            throw new IllegalStateException("Local API mock server is not running");
        }
        return server;
    }
}
