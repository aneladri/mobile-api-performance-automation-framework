package dashboard.enterprise.agent.orchestration.model;

import java.util.List;

public record MultiAgentWorkflow(
        String schemaVersion,
        String workflowId,
        String name,
        String description,
        List<WorkflowStep> steps
) {
    public MultiAgentWorkflow {
        schemaVersion = schemaVersion == null ? "" : schemaVersion.trim();
        workflowId = required(workflowId, "Workflow id");
        name = required(name, "Workflow name");
        description = description == null ? "" : description.trim();
        steps = steps == null ? List.of() : List.copyOf(steps);
        if (steps.isEmpty()) throw new IllegalArgumentException("Workflow requires at least one step.");
    }
    private static String required(String value, String label) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isEmpty()) throw new IllegalArgumentException(label + " is required.");
        return normalized;
    }
}
