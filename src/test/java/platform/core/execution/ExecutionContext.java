package platform.core.execution;

import java.time.Instant;
import java.util.Objects;

/** Correlation identity propagated through execution, events, evidence and reports. */
public record ExecutionContext(
        String executionId,
        String correlationId,
        String traceId,
        Instant startedAt
) {
    public ExecutionContext {
        executionId = requireText(executionId, "executionId");
        correlationId = requireText(correlationId, "correlationId");
        traceId = requireText(traceId, "traceId");
        startedAt = Objects.requireNonNull(startedAt, "startedAt");
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.trim();
    }
}
