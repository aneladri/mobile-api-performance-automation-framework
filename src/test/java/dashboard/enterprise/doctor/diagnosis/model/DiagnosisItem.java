package dashboard.enterprise.doctor.diagnosis.model;

import dashboard.enterprise.doctor.model.HealthSeverity;
import dashboard.enterprise.doctor.model.HealthStatus;

public record DiagnosisItem(
        String id,
        String probeId,
        String capability,
        HealthStatus status,
        HealthSeverity severity,
        DiagnosisPriority priority,
        boolean blocking,
        String issue,
        String rootCause,
        String technicalImpact,
        String businessImpact,
        String recommendedAction,
        String owner,
        String estimatedEffort,
        CorrectiveActionStatus actionStatus,
        String evidenceReference
) {
    public DiagnosisItem {
        id = safe(id);
        probeId = safe(probeId);
        capability = safe(capability);
        issue = safe(issue);
        rootCause = safe(rootCause);
        technicalImpact = safe(technicalImpact);
        businessImpact = safe(businessImpact);
        recommendedAction = safe(recommendedAction);
        owner = safe(owner);
        estimatedEffort = safe(estimatedEffort);
        evidenceReference = safe(evidenceReference);
        if (status == null || severity == null || priority == null || actionStatus == null) {
            throw new IllegalArgumentException("Diagnosis status, severity, priority, and action status are required.");
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
