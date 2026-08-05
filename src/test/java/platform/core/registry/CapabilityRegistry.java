package platform.core.registry;

import java.util.Collection;
import java.util.Optional;
import platform.core.api.Capability;

public interface CapabilityRegistry {

    void register(Capability capability);

    Optional<Capability> find(String capabilityId);

    Collection<Capability> capabilities();

    default Capability require(String capabilityId) {
        return find(capabilityId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Capability is not registered: " + capabilityId));
    }
}
