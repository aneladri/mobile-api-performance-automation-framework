package dashboard.enterprise.agent.skillstudio.model;

import java.util.List;
import java.util.Map;

public record SkillManifest(
        String schemaVersion,
        String skillId,
        String name,
        String description,
        String version,
        List<String> requiredCapabilities,
        List<String> entrypoints,
        Map<String, String> metadata
) {
    public SkillManifest {
        schemaVersion = required(schemaVersion, "Schema version");
        skillId = required(skillId, "Skill id");
        name = required(name, "Skill name");
        description = safe(description);
        version = required(version, "Skill version");
        requiredCapabilities = requiredCapabilities == null ? List.of() : List.copyOf(requiredCapabilities);
        entrypoints = entrypoints == null ? List.of() : List.copyOf(entrypoints);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
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
