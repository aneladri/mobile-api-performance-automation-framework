package platform.core.api;

import platform.core.execution.ExecutionContext;
import platform.core.execution.ExecutionRequest;

/**
 * Stable extension contract implemented by every MAPAF platform capability.
 * Implementations must be stateless or explicitly document their concurrency model.
 */
public interface Capability {

    CapabilityDescriptor descriptor();

    CapabilityResult execute(ExecutionRequest request, ExecutionContext context);
}
