package core.ai.config;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Loads Claude configuration from system properties and environment variables.
 *
 * Priority:
 * 1. System properties
 * 2. Environment variables
 * 3. Safe defaults
 */
public final class ClaudeConfigurationLoader {

    public static final String PROPERTY_ENABLED =
            "claude.enabled";
    public static final String PROPERTY_API_KEY =
            "claude.api.key";
    public static final String PROPERTY_BASE_URL =
            "claude.base.url";
    public static final String PROPERTY_MODEL =
            "claude.model";
    public static final String PROPERTY_MAX_TOKENS =
            "claude.max.tokens";
    public static final String PROPERTY_MAX_RETRIES =
            "claude.max.retries";
    public static final String PROPERTY_TEMPERATURE =
            "claude.temperature";
    public static final String PROPERTY_TIMEOUT_SECONDS =
            "claude.timeout.seconds";

    public static final String ENV_ENABLED =
            "CLAUDE_ENABLED";
    public static final String ENV_API_KEY =
            "ANTHROPIC_API_KEY";
    public static final String ENV_BASE_URL =
            "CLAUDE_BASE_URL";
    public static final String ENV_MODEL =
            "CLAUDE_MODEL";
    public static final String ENV_MAX_TOKENS =
            "CLAUDE_MAX_TOKENS";
    public static final String ENV_MAX_RETRIES =
            "CLAUDE_MAX_RETRIES";
    public static final String ENV_TEMPERATURE =
            "CLAUDE_TEMPERATURE";
    public static final String ENV_TIMEOUT_SECONDS =
            "CLAUDE_TIMEOUT_SECONDS";

    private ClaudeConfigurationLoader() {
        // Utility class.
    }

    public static ClaudeConfiguration load() {
        return load(
                System.getProperties(),
                System.getenv()
        );
    }

    static ClaudeConfiguration load(
            Properties systemProperties,
            Map<String, String> environment) {

        Objects.requireNonNull(
                systemProperties,
                "System properties must not be null"
        );

        Objects.requireNonNull(
                environment,
                "Environment must not be null"
        );

        boolean enabled = parseBoolean(
                resolve(
                        systemProperties,
                        environment,
                        PROPERTY_ENABLED,
                        ENV_ENABLED,
                        "false"
                ),
                PROPERTY_ENABLED
        );

        String apiKey = resolve(
                systemProperties,
                environment,
                PROPERTY_API_KEY,
                ENV_API_KEY,
                null
        );

        String baseUrl = resolve(
                systemProperties,
                environment,
                PROPERTY_BASE_URL,
                ENV_BASE_URL,
                ClaudeConfiguration.DEFAULT_BASE_URL
        );

        String model = resolve(
                systemProperties,
                environment,
                PROPERTY_MODEL,
                ENV_MODEL,
                ClaudeConfiguration.DEFAULT_MODEL
        );

        int maxTokens = parseInteger(
                resolve(
                        systemProperties,
                        environment,
                        PROPERTY_MAX_TOKENS,
                        ENV_MAX_TOKENS,
                        String.valueOf(
                                ClaudeConfiguration.DEFAULT_MAX_TOKENS
                        )
                ),
                PROPERTY_MAX_TOKENS
        );

        int maxRetries = parseInteger(
                resolve(
                        systemProperties,
                        environment,
                        PROPERTY_MAX_RETRIES,
                        ENV_MAX_RETRIES,
                        String.valueOf(
                                ClaudeConfiguration.DEFAULT_MAX_RETRIES
                        )
                ),
                PROPERTY_MAX_RETRIES
        );

        double temperature = parseDouble(
                resolve(
                        systemProperties,
                        environment,
                        PROPERTY_TEMPERATURE,
                        ENV_TEMPERATURE,
                        String.valueOf(
                                ClaudeConfiguration.DEFAULT_TEMPERATURE
                        )
                ),
                PROPERTY_TEMPERATURE
        );

        long timeoutSeconds = parseLong(
                resolve(
                        systemProperties,
                        environment,
                        PROPERTY_TIMEOUT_SECONDS,
                        ENV_TIMEOUT_SECONDS,
                        String.valueOf(
                                ClaudeConfiguration.DEFAULT_TIMEOUT
                                        .toSeconds()
                        )
                ),
                PROPERTY_TIMEOUT_SECONDS
        );

        return ClaudeConfiguration.builder()
                .enabled(enabled)
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .model(model)
                .maxTokens(maxTokens)
                .maxRetries(maxRetries)
                .temperature(temperature)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .build();
    }

    private static String resolve(
            Properties properties,
            Map<String, String> environment,
            String propertyName,
            String environmentName,
            String defaultValue) {

        String propertyValue =
                normalise(properties.getProperty(propertyName));

        if (propertyValue != null) {
            return propertyValue;
        }

        String environmentValue =
                normalise(environment.get(environmentName));

        if (environmentValue != null) {
            return environmentValue;
        }

        return defaultValue;
    }

    private static String normalise(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();

        return trimmed.isEmpty() ? null : trimmed;
    }

    private static boolean parseBoolean(
            String value,
            String propertyName) {

        if ("true".equalsIgnoreCase(value)) {
            return true;
        }

        if ("false".equalsIgnoreCase(value)) {
            return false;
        }

        throw new ClaudeConfigurationException(
                "Invalid boolean value for "
                        + propertyName
                        + ": "
                        + value
        );
    }

    private static int parseInteger(
            String value,
            String propertyName) {

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new ClaudeConfigurationException(
                    "Invalid integer value for "
                            + propertyName
                            + ": "
                            + value,
                    exception
            );
        }
    }

    private static long parseLong(
            String value,
            String propertyName) {

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            throw new ClaudeConfigurationException(
                    "Invalid long value for "
                            + propertyName
                            + ": "
                            + value,
                    exception
            );
        }
    }

    private static double parseDouble(
            String value,
            String propertyName) {

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            throw new ClaudeConfigurationException(
                    "Invalid decimal value for "
                            + propertyName
                            + ": "
                            + value,
                    exception
            );
        }
    }
}
