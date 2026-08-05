package dashboard.enterprise.doctor.probe.dashboard;

import java.util.List;
import java.util.Map;

public record DashboardInspectionResult(
        Map<String, Boolean> reportAvailability,
        Map<String, String> contractVersions,
        List<String> brokenLinks,
        boolean httpCheckEnabled,
        boolean httpReachable,
        String httpEndpoint,
        long durationMillis
) {

    public DashboardInspectionResult {
        reportAvailability = reportAvailability == null
                ? Map.of()
                : Map.copyOf(reportAvailability);

        contractVersions = contractVersions == null
                ? Map.of()
                : Map.copyOf(contractVersions);

        brokenLinks = brokenLinks == null
                ? List.of()
                : List.copyOf(brokenLinks);

        httpEndpoint = httpEndpoint == null
                ? ""
                : httpEndpoint;

        if (durationMillis < 0) {
            throw new IllegalArgumentException(
                    "Dashboard inspection duration cannot be negative."
            );
        }
    }

    public boolean criticalReportsAvailable() {
        return available("command-center")
                && available("executive-summary")
                && available("release-readiness-html")
                && available("release-readiness-json")
                && available("doctor-html")
                && available("doctor-json");
    }

    public boolean supportingReportsAvailable() {
        return available("allure")
                && available("mobile")
                && available("web")
                && available("api")
                && available("performance");
    }

    public boolean linksHealthy() {
        return brokenLinks.isEmpty();
    }

    public boolean httpHealthy() {
        return !httpCheckEnabled || httpReachable;
    }

    private boolean available(String key) {
        return reportAvailability.getOrDefault(key, false);
    }
}
