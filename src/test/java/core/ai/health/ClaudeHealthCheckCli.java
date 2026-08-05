package core.ai.health;

import core.ai.client.ClaudeClient;
import core.ai.client.HttpClaudeTransport;
import core.ai.config.ClaudeConfiguration;
import core.ai.config.ClaudeConfigurationException;
import core.ai.config.ClaudeConfigurationLoader;

/**
 * Command-line entry point for Claude readiness validation.
 */
public final class ClaudeHealthCheckCli {

    private ClaudeHealthCheckCli() {
    }

    public static void main(String[] args) {

        ClaudeHealthResult result;

        try {
            ClaudeConfiguration configuration =
                    ClaudeConfigurationLoader.load();

            ClaudeClient client =
                    new ClaudeClient(
                            configuration,
                            new HttpClaudeTransport()
                    );

            result =
                    new ClaudeHealthCheck(
                            configuration,
                            client
                    ).check();

        } catch (ClaudeConfigurationException exception) {
            result = ClaudeHealthResult.builder()
                    .status(ClaudeHealthStatus.FAIL)
                    .message(exception.getMessage())
                    .latencyMillis(0)
                    .build();
        }

        print(result);

        if (result.getStatus() == ClaudeHealthStatus.FAIL) {
            System.exit(1);
        }
    }

    static void print(ClaudeHealthResult result) {

        System.out.println();
        System.out.println("MAPAF Claude Health Check");
        System.out.println("=========================");
        System.out.println(
                "Status  : " + result.getStatus()
        );
        System.out.println(
                "Model   : "
                        + valueOrUnknown(result.getModel())
        );
        System.out.println(
                "Latency : "
                        + result.getLatencyMillis()
                        + " ms"
        );
        System.out.println(
                "Message : "
                        + valueOrUnknown(result.getMessage())
        );
        System.out.println();
    }

    private static String valueOrUnknown(String value) {
        return value == null || value.isBlank()
                ? "unknown"
                : value;
    }
}
