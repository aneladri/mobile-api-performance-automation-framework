package dashboard.enterprise.doctor.probe.api;

import java.util.List;
import java.util.Map;

public record ApiInspectionResult(
        boolean endpointConfigured,
        boolean connectivityChecked,
        boolean endpointReachable,
        int responseStatus,
        long responseTimeMillis,
        boolean responseBodyPresent,
        boolean authenticationRequired,
        boolean authenticationConfigured,
        boolean enterpriseSummaryAvailable,
        boolean dashboardAvailable,
        boolean failureShowcaseAvailable,
        String endpoint,
        String method,
        String diagnosis,
        long durationMillis,
        List<String> evidenceReferences,
        Map<String, Object> metadata
) {

    public ApiInspectionResult {
        endpoint = safe(endpoint);
        method = safe(method);
        diagnosis = safe(diagnosis);

        evidenceReferences = evidenceReferences == null
                ? List.of()
                : List.copyOf(evidenceReferences);

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);

        if (responseTimeMillis < 0 || durationMillis < 0) {
            throw new IllegalArgumentException(
                    "API timing values cannot be negative."
            );
        }
    }

    public boolean successfulResponse() {
        return endpointReachable
                && responseStatus >= 200
                && responseStatus < 400;
    }

    public boolean authenticationReady() {
        return !authenticationRequired
                || authenticationConfigured;
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
