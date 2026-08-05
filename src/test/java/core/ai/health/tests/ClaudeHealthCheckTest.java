package core.ai.health.tests;

import core.ai.client.ClaudeClient;
import core.ai.client.ClaudeHttpTransport;
import core.ai.config.ClaudeConfiguration;
import core.ai.health.ClaudeHealthCheck;
import core.ai.health.ClaudeHealthResult;
import core.ai.health.ClaudeHealthStatus;
import core.ai.providers.AIResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ClaudeHealthCheckTest {

    @Test
    public void shouldReturnDisabledWhenClaudeIsDisabled() {

        ClaudeConfiguration configuration =
                ClaudeConfiguration.builder()
                        .enabled(false)
                        .build();

        ClaudeHealthResult result =
                new ClaudeHealthCheck(
                        configuration,
                        new ClaudeClient(
                                configuration,
                                successfulTransport()
                        )
                ).check();

        Assert.assertEquals(
                result.getStatus(),
                ClaudeHealthStatus.DISABLED
        );

        Assert.assertFalse(result.isHealthy());
    }

    @Test
    public void shouldReturnPassWhenClaudeResponds() {

        ClaudeConfiguration configuration =
                enabledConfiguration();

        ClaudeHealthResult result =
                new ClaudeHealthCheck(
                        configuration,
                        new ClaudeClient(
                                configuration,
                                successfulTransport()
                        )
                ).check();

        Assert.assertEquals(
                result.getStatus(),
                ClaudeHealthStatus.PASS
        );

        Assert.assertTrue(result.isHealthy());
        Assert.assertEquals(
                result.getMessage(),
                "Claude is available"
        );
    }

    @Test
    public void shouldReturnFailWhenClaudeClientFails() {

        ClaudeConfiguration configuration =
                enabledConfiguration();

        ClaudeHttpTransport transport =
                (ignoredConfiguration, ignoredRequest) ->
                        AIResponse.failure(
                                "Authentication failed"
                        );

        ClaudeHealthResult result =
                new ClaudeHealthCheck(
                        configuration,
                        new ClaudeClient(
                                configuration,
                                transport
                        )
                ).check();

        Assert.assertEquals(
                result.getStatus(),
                ClaudeHealthStatus.FAIL
        );

        Assert.assertEquals(
                result.getMessage(),
                "Authentication failed"
        );
    }

    @Test
    public void shouldReturnWarningForEmptyResponse() {

        ClaudeConfiguration configuration =
                enabledConfiguration();

        ClaudeHttpTransport transport =
                (ignoredConfiguration, ignoredRequest) ->
                        AIResponse.success(" ");

        ClaudeHealthResult result =
                new ClaudeHealthCheck(
                        configuration,
                        new ClaudeClient(
                                configuration,
                                transport
                        )
                ).check();

        Assert.assertEquals(
                result.getStatus(),
                ClaudeHealthStatus.WARN
        );
    }

    @Test
    public void shouldIncludeConfiguredModel() {

        ClaudeConfiguration configuration =
                ClaudeConfiguration.builder()
                        .enabled(true)
                        .apiKey("unit-test-key")
                        .model("health-test-model")
                        .maxRetries(0)
                        .build();

        ClaudeHealthResult result =
                new ClaudeHealthCheck(
                        configuration,
                        new ClaudeClient(
                                configuration,
                                successfulTransport()
                        )
                ).check();

        Assert.assertEquals(
                result.getModel(),
                "health-test-model"
        );
    }

    private ClaudeConfiguration enabledConfiguration() {
        return ClaudeConfiguration.builder()
                .enabled(true)
                .apiKey("unit-test-key")
                .maxRetries(0)
                .build();
    }

    private ClaudeHttpTransport successfulTransport() {
        return (configuration, request) ->
                AIResponse.success("OK");
    }
}
