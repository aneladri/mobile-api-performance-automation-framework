package dashboard.enterprise.doctor.probe.api;

import dashboard.enterprise.doctor.probe.DoctorContext;

public interface ApiInspector {

    ApiInspectionResult inspect(DoctorContext context);
}
