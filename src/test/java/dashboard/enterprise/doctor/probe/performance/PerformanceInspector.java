package dashboard.enterprise.doctor.probe.performance;

import dashboard.enterprise.doctor.probe.DoctorContext;

public interface PerformanceInspector {

    PerformanceInspectionResult inspect(DoctorContext context);
}
