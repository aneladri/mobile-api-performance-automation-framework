# MAPAF Doctor CLI and Executive Report

```text
Health Probes
     |
     v
DoctorSnapshot (mapaf.doctor/v1)
     |
     +--> Executive Overview (mapaf.doctor.executive/v1)
     |
     +--> Diagnosis Center (mapaf.doctor.diagnosis/v1)
     |
     v
mapaf-doctor CLI
     |
     +--> Console status
     +--> executive-report.json
     +--> executive-report.md
     +--> executive-report.html
     +--> executive-report.pdf
```

The CLI is a presentation and delivery layer. It does not change probe results, diagnosis policy, or release decisions.
