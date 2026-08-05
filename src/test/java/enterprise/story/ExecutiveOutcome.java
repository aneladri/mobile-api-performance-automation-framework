package enterprise.story;

import java.util.List;

public record ExecutiveOutcome(
        String status,
        int readinessScore,
        String riskLevel,
        String recommendation,
        List<String> validatedCapabilities,
        List<String> warnings
) {
    public ExecutiveOutcome {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Outcome status must not be blank");
        }
        if (readinessScore < 0 || readinessScore > 100) {
            throw new IllegalArgumentException("Readiness score must be between 0 and 100");
        }
        riskLevel = riskLevel == null ? "UNKNOWN" : riskLevel;
        recommendation = recommendation == null ? "" : recommendation;
        validatedCapabilities = List.copyOf(validatedCapabilities == null ? List.of() : validatedCapabilities);
        warnings = List.copyOf(warnings == null ? List.of() : warnings);
    }
}
