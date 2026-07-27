package core.ai.config.tests;

import core.ai.config.ClaudeConfiguration;
import core.ai.config.ClaudeConfigurationException;
import core.ai.config.ClaudeConfigurationLoader;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ClaudeConfigurationLoaderTest {

    @Test
    public void shouldUseDefaultsWhenNoConfigurationExists() {
        ClaudeConfiguration configuration =
                load(new Properties(), new HashMap<>());

        Assert.assertFalse(configuration.isEnabled());
        Assert.assertEquals(
                configuration.getModel(),
                ClaudeConfiguration.DEFAULT_MODEL
        );
        Assert.assertEquals(
                configuration.getMaxTokens(),
                ClaudeConfiguration.DEFAULT_MAX_TOKENS
        );
    }

    @Test
    public void shouldLoadEnvironmentVariables() {
        Map<String, String> environment = new HashMap<>();

        environment.put("CLAUDE_ENABLED", "true");
        environment.put("ANTHROPIC_API_KEY", "environment-key");
        environment.put("CLAUDE_MODEL", "environment-model");
        environment.put("CLAUDE_MAX_TOKENS", "2500");

        ClaudeConfiguration configuration =
                load(new Properties(), environment);

        Assert.assertTrue(configuration.isEnabled());
        Assert.assertEquals(
                configuration.getApiKey(),
                "environment-key"
        );
        Assert.assertEquals(
                configuration.getModel(),
                "environment-model"
        );
        Assert.assertEquals(
                configuration.getMaxTokens(),
                2500
        );
    }

    @Test
    public void shouldPreferSystemPropertiesOverEnvironment() {
        Properties properties = new Properties();

        properties.setProperty(
                "claude.enabled",
                "true"
        );
        properties.setProperty(
                "claude.api.key",
                "property-key"
        );
        properties.setProperty(
                "claude.model",
                "property-model"
        );

        Map<String, String> environment = new HashMap<>();

        environment.put("CLAUDE_ENABLED", "true");
        environment.put("ANTHROPIC_API_KEY", "environment-key");
        environment.put("CLAUDE_MODEL", "environment-model");

        ClaudeConfiguration configuration =
                load(properties, environment);

        Assert.assertEquals(
                configuration.getApiKey(),
                "property-key"
        );

        Assert.assertEquals(
                configuration.getModel(),
                "property-model"
        );
    }

    @Test(
            expectedExceptions =
                    ClaudeConfigurationException.class
    )
    public void shouldRejectInvalidBooleanValue() {
        Properties properties = new Properties();

        properties.setProperty(
                "claude.enabled",
                "sometimes"
        );

        load(properties, new HashMap<>());
    }

    private ClaudeConfiguration load(
            Properties properties,
            Map<String, String> environment) {

        try {
            var method =
                    ClaudeConfigurationLoader.class
                            .getDeclaredMethod(
                                    "load",
                                    Properties.class,
                                    Map.class
                            );

            method.setAccessible(true);

            return (ClaudeConfiguration)
                    method.invoke(
                            null,
                            properties,
                            environment
                    );

        } catch (ReflectiveOperationException exception) {
            Throwable cause = exception.getCause();

            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }

            throw new RuntimeException(exception);
        }
    }
}
