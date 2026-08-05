package dashboard.enterprise.agent.skillstudio.model;

import java.util.List;

public record SkillEvaluationResult(
        String schemaVersion,
        String skillCoordinate,
        int totalTests,
        int passedTests,
        int failedTests,
        double score,
        List<String> evidence
) {
    public SkillEvaluationResult {
        schemaVersion = schemaVersion == null ? "" : schemaVersion.trim();
        skillCoordinate = skillCoordinate == null ? "" : skillCoordinate.trim();
        evidence = evidence == null ? List.of() : List.copyOf(evidence);
        if (totalTests < 0 || passedTests < 0 || failedTests < 0) {
            throw new IllegalArgumentException("Skill evaluation counts cannot be negative.");
        }
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Skill evaluation score must be between 0 and 100.");
        }
    }

    public boolean passed() {
        return failedTests == 0 && totalTests > 0;
    }
}
