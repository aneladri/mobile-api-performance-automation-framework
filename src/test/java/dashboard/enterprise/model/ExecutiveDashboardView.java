package dashboard.enterprise.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public record ExecutiveDashboardView(
        String application,
        String environment,
        String generatedAt,
        List<EnterpriseModuleView> modules,
        int readinessScore,
        String risk,
        String recommendation,
        String overallStatus,
        double overallSuccessRate,
        long totalDurationMillis,
        String narrative
) {
    public ExecutiveDashboardView {
        generatedAt = generatedAt == null ? Instant.now().toString() : generatedAt;
        modules = modules == null ? new ArrayList<>() : new ArrayList<>(modules);
    }
}
