package dashboard.enterprise.doctor.probe.claude;

import dashboard.enterprise.doctor.probe.DoctorContext;

public interface ClaudeInspector {

    ClaudeInspectionResult inspect(DoctorContext context);
}
