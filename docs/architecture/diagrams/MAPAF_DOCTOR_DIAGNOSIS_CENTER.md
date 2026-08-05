# MAPAF Doctor Diagnosis & Corrective Action Center

```text
DoctorSnapshot (mapaf.doctor/v1)
              |
              v
DefaultDoctorDiagnosisEngine
              |
              v
DiagnosisSnapshot (mapaf.doctor.diagnosis/v1)
              |
        +-----+-----+
        |           |
        v           v
doctor-diagnosis  doctor-diagnosis
      .json             .html
```

The diagnosis layer does not execute probes or modify health results. It converts existing Doctor evidence into prioritized operational actions with owner, impact, effort, and action status.
