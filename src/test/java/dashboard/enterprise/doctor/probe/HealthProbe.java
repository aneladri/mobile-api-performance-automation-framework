package dashboard.enterprise.doctor.probe;

import dashboard.enterprise.doctor.model.HealthProbeDefinition;
import dashboard.enterprise.doctor.model.HealthProbeResult;

/**
 * Extension contract implemented by all MAPAF Doctor probes.
 */
public interface HealthProbe {

    HealthProbeDefinition definition();

    HealthProbeResult execute(DoctorContext context);
}
