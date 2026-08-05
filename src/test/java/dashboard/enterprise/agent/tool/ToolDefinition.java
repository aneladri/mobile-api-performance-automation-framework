package dashboard.enterprise.agent.tool;

public record ToolDefinition(
        String schemaVersion,
        String toolId,
        String name,
        String description,
        PermissionLevel permissionLevel
) {
    public ToolDefinition {
        schemaVersion = safe(schemaVersion);
        toolId = required(toolId, "Tool id");
        name = required(name, "Tool name");
        description = safe(description);
        permissionLevel = permissionLevel == null
                ? PermissionLevel.PROHIBITED
                : permissionLevel;
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
