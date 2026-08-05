package architecture.governance;

import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;
import platform.core.api.Capability;
import platform.core.api.CapabilityDescriptor;
import platform.core.api.CapabilityResult;
import platform.core.api.CapabilityType;
import platform.core.event.InMemoryPlatformEventBus;
import platform.core.event.PlatformEventType;
import platform.core.execution.DefaultPlatformExecutionEngine;
import platform.core.execution.ExecutionRequest;
import platform.core.execution.ExecutionStatus;
import platform.core.registry.InMemoryCapabilityRegistry;

public class PlatformCoreRuntimeTest {

    @Test
    public void shouldExecuteRegisteredCapabilityWithCorrelationAndEvents() {
        InMemoryCapabilityRegistry registry = new InMemoryCapabilityRegistry();
        InMemoryPlatformEventBus eventBus = new InMemoryPlatformEventBus();
        registry.register(passingCapability());

        var engine = new DefaultPlatformExecutionEngine(registry, eventBus);
        var outcome = engine.execute(new ExecutionRequest(
                "sample.mobile",
                "RoomScan smoke validation",
                "QA",
                Map.of("device", "emulator-5554")
        ));

        Assert.assertEquals(outcome.capabilityResult().status(), ExecutionStatus.PASSED);
        Assert.assertTrue(outcome.context().executionId().startsWith("RUN-"));
        Assert.assertEquals(outcome.context().traceId().length(), 32);
        Assert.assertEquals(eventBus.history().size(), 4);
        Assert.assertEquals(eventBus.history().get(0).type(), PlatformEventType.EXECUTION_STARTED);
        Assert.assertEquals(eventBus.history().get(3).type(), PlatformEventType.EXECUTION_COMPLETED);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void shouldRejectDuplicateCapabilityRegistration() {
        InMemoryCapabilityRegistry registry = new InMemoryCapabilityRegistry();
        registry.register(passingCapability());
        registry.register(passingCapability());
    }

    private Capability passingCapability() {
        return new Capability() {
            @Override
            public CapabilityDescriptor descriptor() {
                return new CapabilityDescriptor(
                        "sample.mobile",
                        "Sample Mobile Capability",
                        "1.0.0",
                        CapabilityType.MOBILE
                );
            }

            @Override
            public CapabilityResult execute(
                    ExecutionRequest request,
                    platform.core.execution.ExecutionContext context) {
                return CapabilityResult.passed("Sample capability completed");
            }
        };
    }
}
