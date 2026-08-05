package dashboard.enterprise.doctor.probe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class InMemoryHealthProbeRegistry
        implements HealthProbeRegistry {

    private final Map<String, HealthProbe> probes =
            new LinkedHashMap<>();

    @Override
    public synchronized void register(HealthProbe probe) {
        if (probe == null) {
            throw new IllegalArgumentException(
                    "Health probe is required."
            );
        }

        String id = probe.definition().id();

        if (probes.containsKey(id)) {
            throw new IllegalArgumentException(
                    "Health probe already registered: " + id
            );
        }

        probes.put(id, probe);
    }

    @Override
    public synchronized HealthProbe findById(String probeId) {
        HealthProbe probe = probes.get(probeId);

        if (probe == null) {
            throw new IllegalArgumentException(
                    "Unknown health probe: " + probeId
            );
        }

        return probe;
    }

    @Override
    public synchronized List<HealthProbe> enabledProbes() {
        return probes.values().stream()
                .filter(probe -> probe.definition().enabled())
                .toList();
    }
}
