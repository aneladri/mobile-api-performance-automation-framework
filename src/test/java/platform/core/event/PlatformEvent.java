package platform.core.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import platform.core.execution.ExecutionContext;

/** Immutable platform event designed for later OpenTelemetry and external-bus adapters. */
public record PlatformEvent(
        String eventId,
        PlatformEventType type,
        String executionId,
        String correlationId,
        String traceId,
        String capabilityId,
        Instant occurredAt,
        String message
) {
    public PlatformEvent {
        eventId = Objects.requireNonNull(eventId, "eventId");
        type = Objects.requireNonNull(type, "type");
        executionId = Objects.requireNonNull(executionId, "executionId");
        correlationId = Objects.requireNonNull(correlationId, "correlationId");
        traceId = Objects.requireNonNull(traceId, "traceId");
        capabilityId = Objects.requireNonNull(capabilityId, "capabilityId");
        occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
        message = message == null ? "" : message;
    }

    public static PlatformEvent create(
            PlatformEventType type,
            ExecutionContext context,
            String capabilityId,
            String message) {
        return new PlatformEvent(
                UUID.randomUUID().toString(),
                type,
                context.executionId(),
                context.correlationId(),
                context.traceId(),
                capabilityId,
                Instant.now(),
                message
        );
    }
}
