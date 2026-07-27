package core.ai.config;

import java.time.Duration;
import java.util.Objects;

/**
 * Immutable configuration used by Claude-based AI components.
 */
public final class ClaudeConfiguration {

    public static final String DEFAULT_BASE_URL =
            "https://api.anthropic.com/v1/messages";

    public static final String DEFAULT_MODEL =
            "claude-sonnet-4-5";

    public static final int DEFAULT_MAX_TOKENS = 1024;
    public static final int DEFAULT_MAX_RETRIES = 2;
    public static final double DEFAULT_TEMPERATURE = 0.0;
    public static final Duration DEFAULT_TIMEOUT =
            Duration.ofSeconds(30);

    private final boolean enabled;
    private final String apiKey;
    private final String baseUrl;
    private final String model;
    private final int maxTokens;
    private final int maxRetries;
    private final double temperature;
    private final Duration timeout;

    private ClaudeConfiguration(Builder builder) {
        this.enabled = builder.enabled;
        this.apiKey = normalise(builder.apiKey);
        this.baseUrl = requireText(
                builder.baseUrl,
                "Claude base URL must not be blank"
        );
        this.model = requireText(
                builder.model,
                "Claude model must not be blank"
        );
        this.maxTokens = validateMaxTokens(builder.maxTokens);
        this.maxRetries = validateMaxRetries(builder.maxRetries);
        this.temperature =
                validateTemperature(builder.temperature);
        this.timeout = validateTimeout(builder.timeout);

        validateApiKey();
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getModel() {
        return model;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public double getTemperature() {
        return temperature;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public boolean hasApiKey() {
        return apiKey != null && !apiKey.isBlank();
    }

    /**
     * Returns a safe summary that never exposes the API key.
     */
    public String toSafeSummary() {
        return "ClaudeConfiguration{" +
                "enabled=" + enabled +
                ", apiKeyConfigured=" + hasApiKey() +
                ", baseUrl='" + baseUrl + '\'' +
                ", model='" + model + '\'' +
                ", maxTokens=" + maxTokens +
                ", maxRetries=" + maxRetries +
                ", temperature=" + temperature +
                ", timeoutSeconds=" + timeout.toSeconds() +
                '}';
    }

    @Override
    public String toString() {
        return toSafeSummary();
    }

    private void validateApiKey() {
        if (enabled && !hasApiKey()) {
            throw new ClaudeConfigurationException(
                    "Claude API key is required when Claude is enabled"
            );
        }
    }

    private static String requireText(
            String value,
            String message) {

        String normalised = normalise(value);

        if (normalised == null || normalised.isBlank()) {
            throw new ClaudeConfigurationException(message);
        }

        return normalised;
    }

    private static String normalise(String value) {
        return value == null ? null : value.trim();
    }

    private static int validateMaxTokens(int value) {
        if (value <= 0) {
            throw new ClaudeConfigurationException(
                    "Claude max tokens must be greater than zero"
            );
        }

        return value;
    }

    private static int validateMaxRetries(int value) {
        if (value < 0) {
            throw new ClaudeConfigurationException(
                    "Claude max retries must not be negative"
            );
        }

        return value;
    }

    private static double validateTemperature(double value) {
        if (value < 0.0 || value > 1.0) {
            throw new ClaudeConfigurationException(
                    "Claude temperature must be between 0.0 and 1.0"
            );
        }

        return value;
    }

    private static Duration validateTimeout(Duration value) {
        Objects.requireNonNull(
                value,
                "Claude timeout must not be null"
        );

        if (value.isZero() || value.isNegative()) {
            throw new ClaudeConfigurationException(
                    "Claude timeout must be greater than zero"
            );
        }

        return value;
    }

    public static final class Builder {

        private boolean enabled = false;
        private String apiKey;
        private String baseUrl = DEFAULT_BASE_URL;
        private String model = DEFAULT_MODEL;
        private int maxTokens = DEFAULT_MAX_TOKENS;
        private int maxRetries = DEFAULT_MAX_RETRIES;
        private double temperature = DEFAULT_TEMPERATURE;
        private Duration timeout = DEFAULT_TIMEOUT;

        private Builder() {
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder maxTokens(int maxTokens) {
            this.maxTokens = maxTokens;
            return this;
        }

        public Builder maxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        public Builder temperature(double temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public ClaudeConfiguration build() {
            return new ClaudeConfiguration(this);
        }
    }
}
