package platform.core.execution;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import platform.core.api.CapabilityResult;

/** Completed execution envelope returned by the platform core. */
public record ExecutionOutcome(
        ExecutionContext context,
        CapabilityResult capabilityResult,
        Instant completedAt,
        Duration duration
) {
    public ExecutionOutcome {
        context = Objects.requireNonNull(context, "context");
        capabilityResult = Objects.requireNonNull(capabilityResult, "capabilityResult");
        completedAt = Objects.requireNonNull(completedAt, "completedAt");
        duration = Objects.requireNonNull(duration, "duration");
    }
}
