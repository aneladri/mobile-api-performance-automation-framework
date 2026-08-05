package dashboard.enterprise.agent.skill;

import java.util.List;

public record SkillDefinition(
        String schemaVersion,
        String skillId,
        String name,
        String description,
        List<String> requiredToolIds,
        List<String> instructions
) {
    public SkillDefinition {
        schemaVersion = safe(schemaVersion);
        skillId = required(skillId, "Skill id");
        name = required(name, "Skill name");
        description = safe(description);
        requiredToolIds = requiredToolIds == null ? List.of() : List.copyOf(requiredToolIds);
        instructions = instructions == null ? List.of() : List.copyOf(instructions);
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private static String required(String value, String label) {
        String normalized = safe(value);
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return normalized;
    }
}
