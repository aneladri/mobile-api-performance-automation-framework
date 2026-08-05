package core.ai.config.tests;

import core.ai.config.ClaudeConfiguration;
import core.ai.config.ClaudeConfigurationException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class ClaudeConfigurationTest {

    @Test
    public void shouldCreateDisabledConfigurationWithoutApiKey() {
        ClaudeConfiguration configuration =
                ClaudeConfiguration.builder()
                        .enabled(false)
                        .build();

        Assert.assertFalse(configuration.isEnabled());
        Assert.assertFalse(configuration.hasApiKey());
        Assert.assertEquals(
                configuration.getBaseUrl(),
                ClaudeConfiguration.DEFAULT_BASE_URL
        );
        Assert.assertEquals(
                configuration.getModel(),
                ClaudeConfiguration.DEFAULT_MODEL
        );
    }

    @Test
    public void shouldCreateEnabledConfigurationWithApiKey() {
        ClaudeConfiguration configuration =
                ClaudeConfiguration.builder()
                        .enabled(true)
                        .apiKey("test-key")
                        .model("test-model")
                        .maxTokens(2048)
                        .maxRetries(3)
                        .temperature(0.2)
                        .timeout(Duration.ofSeconds(45))
                        .build();

        Assert.assertTrue(configuration.isEnabled());
        Assert.assertTrue(configuration.hasApiKey());
        Assert.assertEquals(
                configuration.getApiKey(),
                "test-key"
        );
        Assert.assertEquals(
                configuration.getModel(),
                "test-model"
        );
        Assert.assertEquals(
                configuration.getMaxTokens(),
                2048
        );
    }

    @Test(
            expectedExceptions =
                    ClaudeConfigurationException.class
    )
    public void shouldRejectEnabledConfigurationWithoutApiKey() {
        ClaudeConfiguration.builder()
                .enabled(true)
                .build();
    }

    @Test(
            expectedExceptions =
                    ClaudeConfigurationException.class
    )
    public void shouldRejectInvalidTemperature() {
        ClaudeConfiguration.builder()
                .temperature(1.5)
                .build();
    }

    @Test
    public void shouldNotExposeApiKeyInStringOutput() {
        ClaudeConfiguration configuration =
                ClaudeConfiguration.builder()
                        .enabled(true)
                        .apiKey("super-secret-key")
                        .build();

        String safeOutput = configuration.toString();

        Assert.assertFalse(
                safeOutput.contains("super-secret-key")
        );

        Assert.assertTrue(
                safeOutput.contains("apiKeyConfigured=true")
        );
    }
}
