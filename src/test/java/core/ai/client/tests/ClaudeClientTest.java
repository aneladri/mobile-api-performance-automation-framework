package core.ai.client.tests;

import core.ai.client.ClaudeClient;
import core.ai.client.ClaudeClientException;
import core.ai.client.ClaudeHttpTransport;
import core.ai.config.ClaudeConfiguration;
import core.ai.providers.AIRequest;
import core.ai.providers.AIResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class ClaudeClientTest {

    @Test
    public void shouldReturnFailureWhenClaudeIsDisabled() {

        ClaudeClient client =
                new ClaudeClient(
                        disabledConfiguration(),
                        successfulTransport()
                );

        AIResponse response =
                client.complete(
                        new AIRequest(
                                "system",
                                "prompt"
                        )
                );

        Assert.assertFalse(response.isSuccessful());
        Assert.assertEquals(
                response.getErrorMessage(),
                "Claude integration is disabled"
        );
    }

    @Test
    public void shouldRejectBlankPrompt() {

        ClaudeClient client =
                new ClaudeClient(
                        enabledConfiguration(0),
                        successfulTransport()
                );

        AIResponse response =
                client.complete(
                        new AIRequest(
                                "system",
                                " "
                        )
                );

        Assert.assertFalse(response.isSuccessful());
        Assert.assertEquals(
                response.getErrorMessage(),
                "Claude prompt must not be blank"
        );
    }

    @Test
    public void shouldReturnSuccessfulTransportResponse() {

        ClaudeClient client =
                new ClaudeClient(
                        enabledConfiguration(0),
                        successfulTransport()
                );

        AIResponse response =
                client.complete(
                        new AIRequest(
                                "system",
                                "prompt"
                        )
                );

        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals(
                response.getContent(),
                "completed"
        );
    }

    @Test
    public void shouldRetryTransientFailure() {

        AtomicInteger attempts =
                new AtomicInteger();

        ClaudeHttpTransport transport =
                (configuration, request) -> {

                    if (attempts.incrementAndGet() == 1) {
                        throw new ClaudeClientException(
                                "Temporary failure",
                                503,
                                true
                        );
                    }

                    return AIResponse.success("recovered");
                };

        ClaudeClient client =
                new ClaudeClient(
                        enabledConfiguration(2),
                        transport
                );

        AIResponse response =
                client.complete(
                        new AIRequest(
                                "system",
                                "prompt"
                        )
                );

        Assert.assertTrue(response.isSuccessful());
        Assert.assertEquals(
                response.getContent(),
                "recovered"
        );
        Assert.assertEquals(attempts.get(), 2);
    }

    @Test
    public void shouldNotRetryPermanentFailure() {

        AtomicInteger attempts =
                new AtomicInteger();

        ClaudeHttpTransport transport =
                (configuration, request) -> {

                    attempts.incrementAndGet();

                    throw new ClaudeClientException(
                            "Unauthorised",
                            401,
                            false
                    );
                };

        ClaudeClient client =
                new ClaudeClient(
                        enabledConfiguration(3),
                        transport
                );

        AIResponse response =
                client.complete(
                        new AIRequest(
                                "system",
                                "prompt"
                        )
                );

        Assert.assertFalse(response.isSuccessful());
        Assert.assertEquals(
                response.getErrorMessage(),
                "Unauthorised"
        );
        Assert.assertEquals(attempts.get(), 1);
    }

    @Test
    public void shouldStopAfterConfiguredRetries() {

        AtomicInteger attempts =
                new AtomicInteger();

        ClaudeHttpTransport transport =
                (configuration, request) -> {

                    attempts.incrementAndGet();

                    throw new ClaudeClientException(
                            "Service unavailable",
                            503,
                            true
                    );
                };

        ClaudeClient client =
                new ClaudeClient(
                        enabledConfiguration(2),
                        transport
                );

        AIResponse response =
                client.complete(
                        new AIRequest(
                                "system",
                                "prompt"
                        )
                );

        Assert.assertFalse(response.isSuccessful());
        Assert.assertEquals(attempts.get(), 3);
    }

    private ClaudeConfiguration enabledConfiguration(
            int retries) {

        return ClaudeConfiguration.builder()
                .enabled(true)
                .apiKey("unit-test-key")
                .maxRetries(retries)
                .build();
    }

    private ClaudeConfiguration disabledConfiguration() {
        return ClaudeConfiguration.builder()
                .enabled(false)
                .build();
    }

    private ClaudeHttpTransport successfulTransport() {
        return (configuration, request) ->
                AIResponse.success("completed");
    }
}
