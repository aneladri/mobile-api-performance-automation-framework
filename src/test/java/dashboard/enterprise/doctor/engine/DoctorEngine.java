package dashboard.enterprise.doctor.engine;

import dashboard.enterprise.doctor.model.DoctorSnapshot;
import dashboard.enterprise.doctor.probe.DoctorContext;

public interface DoctorEngine {

    DoctorSnapshot assess(DoctorContext context);
}
