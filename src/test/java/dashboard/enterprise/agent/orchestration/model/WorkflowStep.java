package dashboard.enterprise.agent.orchestration.model;

import java.util.Map;

public record WorkflowStep(
        String schemaVersion,
        String stepId,
        String name,
        String agentId,
        String skillId,
        String toolId,
        WorkflowFailurePolicy failurePolicy,
        boolean approvalRequired,
        boolean optional,
        Map<String, Object> input
) {
    public WorkflowStep {
        schemaVersion = safe(schemaVersion);
        stepId = required(stepId, "Step id");
        name = required(name, "Step name");
        agentId = required(agentId, "Agent id");
        skillId = required(skillId, "Skill id");
        toolId = required(toolId, "Tool id");
        failurePolicy = failurePolicy == null ? WorkflowFailurePolicy.STOP_ON_FAILURE : failurePolicy;
        input = input == null ? Map.of() : Map.copyOf(input);
    }

    private static String safe(String value) { return value == null ? "" : value.trim(); }
    private static String required(String value, String label) {
        String normalized = safe(value);
        if (normalized.isEmpty()) throw new IllegalArgumentException(label + " is required.");
        return normalized;
    }
}
