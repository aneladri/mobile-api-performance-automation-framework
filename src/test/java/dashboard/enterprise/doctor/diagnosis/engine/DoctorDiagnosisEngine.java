package dashboard.enterprise.doctor.diagnosis.engine;

import dashboard.enterprise.doctor.diagnosis.model.DiagnosisSnapshot;
import dashboard.enterprise.doctor.model.DoctorSnapshot;

public interface DoctorDiagnosisEngine {
    DiagnosisSnapshot diagnose(DoctorSnapshot snapshot);
}
