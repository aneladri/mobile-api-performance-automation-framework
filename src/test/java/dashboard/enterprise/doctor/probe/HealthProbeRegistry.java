package dashboard.enterprise.doctor.probe;

import java.util.List;

public interface HealthProbeRegistry {

    void register(HealthProbe probe);

    HealthProbe findById(String probeId);

    List<HealthProbe> enabledProbes();
}
