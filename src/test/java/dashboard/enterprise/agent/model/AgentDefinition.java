package dashboard.enterprise.agent.model;

import java.util.List;

public record AgentDefinition(
        String schemaVersion,
        String agentId,
        String name,
        String description,
        List<String> skillIds,
        List<String> allowedToolIds,
        ApprovalPolicy approvalPolicy
) {
    public AgentDefinition {
        schemaVersion = safe(schemaVersion);
        agentId = required(agentId, "Agent id");
        name = required(name, "Agent name");
        description = safe(description);
        skillIds = skillIds == null ? List.of() : List.copyOf(skillIds);
        allowedToolIds = allowedToolIds == null ? List.of() : List.copyOf(allowedToolIds);
        approvalPolicy = approvalPolicy == null
                ? ApprovalPolicy.HUMAN_REQUIRED_FOR_WRITES
                : approvalPolicy;
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
