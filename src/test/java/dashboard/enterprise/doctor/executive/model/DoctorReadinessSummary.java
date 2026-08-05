package dashboard.enterprise.doctor.executive.model;

public record DoctorReadinessSummary(
        boolean platformReady,
        boolean executionReady,
        boolean evidenceReady,
        boolean releaseReady,
        boolean aiReady,
        boolean mobileReady,
        boolean apiReady,
        boolean performanceReady
) {
}
