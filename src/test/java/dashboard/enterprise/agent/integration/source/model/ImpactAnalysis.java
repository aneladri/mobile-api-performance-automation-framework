package dashboard.enterprise.agent.integration.source.model;

import java.util.List;

public record ImpactAnalysis(
        String schemaVersion,
        String risk,
        int riskScore,
        List<String> affectedModules,
        List<String> recommendedSuites,
        List<String> reasons,
        int estimatedMinutes,
        boolean mergeBlockRecommended
) {
    public ImpactAnalysis {
        schemaVersion = safe(schemaVersion);
        risk = safe(risk);
        affectedModules = affectedModules == null ? List.of() : List.copyOf(affectedModules);
        recommendedSuites = recommendedSuites == null ? List.of() : List.copyOf(recommendedSuites);
        reasons = reasons == null ? List.of() : List.copyOf(reasons);
        riskScore = Math.max(0, Math.min(100, riskScore));
        estimatedMinutes = Math.max(0, estimatedMinutes);
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
