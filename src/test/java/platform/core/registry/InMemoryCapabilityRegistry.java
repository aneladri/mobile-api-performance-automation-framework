package platform.core.registry;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import platform.core.api.Capability;

/** Deterministic registry with duplicate protection. */
public final class InMemoryCapabilityRegistry implements CapabilityRegistry {

    private final Map<String, Capability> capabilities = new LinkedHashMap<>();

    @Override
    public synchronized void register(Capability capability) {
        Capability safeCapability = Objects.requireNonNull(capability, "capability");
        String id = safeCapability.descriptor().id();
        if (capabilities.containsKey(id)) {
            throw new IllegalStateException("Capability already registered: " + id);
        }
        capabilities.put(id, safeCapability);
    }

    @Override
    public synchronized Optional<Capability> find(String capabilityId) {
        return Optional.ofNullable(capabilities.get(capabilityId));
    }

    @Override
    public synchronized Collection<Capability> capabilities() {
        return ListCopy.copy(capabilities.values());
    }

    private static final class ListCopy {
        private ListCopy() {
        }

        private static <T> Collection<T> copy(Collection<T> values) {
            return java.util.List.copyOf(values);
        }
    }
}
