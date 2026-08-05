package dashboard.enterprise.model;

import java.util.LinkedHashMap;
import java.util.Map;

public record EnterpriseModuleView(
        String key,
        String name,
        String status,
        String scenario,
        String environment,
        double successRate,
        long durationMillis,
        int total,
        int passed,
        int failed,
        Map<String, Object> metrics,
        Map<String, String> links
) {
    public EnterpriseModuleView {
        metrics = metrics == null ? new LinkedHashMap<>() : new LinkedHashMap<>(metrics);
        links = links == null ? new LinkedHashMap<>() : new LinkedHashMap<>(links);
    }

    public boolean passedExecution() {
        return "PASS".equalsIgnoreCase(status) || "PASSED".equalsIgnoreCase(status);
    }

    public boolean hasRun() {
        return !"NOT_RUN".equalsIgnoreCase(status);
    }
}
