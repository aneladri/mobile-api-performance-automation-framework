package platform.core.execution;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import platform.core.api.Capability;
import platform.core.api.CapabilityResult;
import platform.core.event.PlatformEvent;
import platform.core.event.PlatformEventPublisher;
import platform.core.event.PlatformEventType;
import platform.core.registry.CapabilityRegistry;

/**
 * Minimal production-shaped execution kernel. It resolves a capability, creates
 * correlation identity, emits lifecycle events, invokes the capability and returns
 * a normalized outcome.
 */
public final class DefaultPlatformExecutionEngine implements PlatformExecutionEngine {

    private final CapabilityRegistry registry;
    private final PlatformEventPublisher eventPublisher;

    public DefaultPlatformExecutionEngine(
            CapabilityRegistry registry,
            PlatformEventPublisher eventPublisher) {
        this.registry = Objects.requireNonNull(registry, "registry");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher");
    }

    @Override
    public ExecutionOutcome execute(ExecutionRequest request) {
        Objects.requireNonNull(request, "request");
        ExecutionContext context = ExecutionIdentityFactory.create();
        Capability capability = registry.require(request.capabilityId());

        publish(PlatformEventType.EXECUTION_STARTED, context, request.capabilityId(), "Execution started");
        publish(PlatformEventType.CAPABILITY_STARTED, context, request.capabilityId(), "Capability started");

        CapabilityResult result;
        try {
            result = capability.execute(request, context);
            PlatformEventType completionType = result.status() == ExecutionStatus.PASSED
                    ? PlatformEventType.CAPABILITY_COMPLETED
                    : PlatformEventType.CAPABILITY_FAILED;
            publish(completionType, context, request.capabilityId(), result.summary());
        } catch (RuntimeException exception) {
            result = CapabilityResult.failed(exception.getMessage());
            publish(PlatformEventType.CAPABILITY_FAILED, context, request.capabilityId(), exception.getMessage());
        }

        Instant completedAt = Instant.now();
        publish(PlatformEventType.EXECUTION_COMPLETED, context, request.capabilityId(), result.status().name());
        return new ExecutionOutcome(
                context,
                result,
                completedAt,
                Duration.between(context.startedAt(), completedAt)
        );
    }

    private void publish(
            PlatformEventType type,
            ExecutionContext context,
            String capabilityId,
            String message) {
        eventPublisher.publish(PlatformEvent.create(type, context, capabilityId, message));
    }
}
