package dashboard.enterprise.agent.runtime.model;

import dashboard.enterprise.agent.tool.PermissionLevel;

public record ToolCapability(
        String schemaVersion,
        String capabilityId,
        String name,
        String description,
        PermissionLevel permissionLevel
) {
    public ToolCapability {
        schemaVersion = safe(schemaVersion);
        capabilityId = required(capabilityId, "Capability id");
        name = required(name, "Capability name");
        description = safe(description);
        permissionLevel = permissionLevel == null ? PermissionLevel.PROHIBITED : permissionLevel;
    }

    private static String safe(String value) { return value == null ? "" : value.trim(); }
    private static String required(String value, String label) {
        String normalized = safe(value);
        if (normalized.isEmpty()) throw new IllegalArgumentException(label + " is required.");
        return normalized;
    }
}
