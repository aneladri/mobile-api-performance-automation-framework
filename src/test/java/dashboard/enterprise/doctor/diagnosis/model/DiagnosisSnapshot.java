package dashboard.enterprise.doctor.diagnosis.model;

import java.util.List;
import java.util.Map;

public record DiagnosisSnapshot(
        String schemaVersion,
        String generatedAt,
        String product,
        String platformVersion,
        String environment,
        int totalIssues,
        int blockingIssues,
        int warnings,
        int openActions,
        String executiveSummary,
        List<DiagnosisItem> diagnoses,
        Map<String, String> capabilityImpact,
        List<String> immediateActions,
        String overviewReport,
        String detailedReport
) {
    public DiagnosisSnapshot {
        schemaVersion = safe(schemaVersion);
        generatedAt = safe(generatedAt);
        product = safe(product);
        platformVersion = safe(platformVersion);
        environment = safe(environment);
        executiveSummary = safe(executiveSummary);
        overviewReport = safe(overviewReport);
        detailedReport = safe(detailedReport);
        diagnoses = diagnoses == null ? List.of() : List.copyOf(diagnoses);
        capabilityImpact = capabilityImpact == null ? Map.of() : Map.copyOf(capabilityImpact);
        immediateActions = immediateActions == null ? List.of() : List.copyOf(immediateActions);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
