package core.ai.health;

import java.util.Objects;

/**
 * Structured result returned by the Claude health check.
 */
public final class ClaudeHealthResult {

    private final ClaudeHealthStatus status;
    private final String message;
    private final String model;
    private final long latencyMillis;

    private ClaudeHealthResult(Builder builder) {
        this.status = Objects.requireNonNull(
                builder.status,
                "Claude health status must not be null"
        );

        this.message = normalise(builder.message);
        this.model = normalise(builder.model);
        this.latencyMillis = validateLatency(
                builder.latencyMillis
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    public ClaudeHealthStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getModel() {
        return model;
    }

    public long getLatencyMillis() {
        return latencyMillis;
    }

    public boolean isHealthy() {
        return status == ClaudeHealthStatus.PASS;
    }

    private static String normalise(String value) {
        return value == null ? null : value.trim();
    }

    private static long validateLatency(long value) {
        if (value < 0) {
            throw new IllegalArgumentException(
                    "Claude latency must not be negative"
            );
        }

        return value;
    }

    public static final class Builder {

        private ClaudeHealthStatus status;
        private String message;
        private String model;
        private long latencyMillis;

        private Builder() {
        }

        public Builder status(ClaudeHealthStatus status) {
            this.status = status;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder latencyMillis(long latencyMillis) {
            this.latencyMillis = latencyMillis;
            return this;
        }

        public ClaudeHealthResult build() {
            return new ClaudeHealthResult(this);
        }
    }
}
