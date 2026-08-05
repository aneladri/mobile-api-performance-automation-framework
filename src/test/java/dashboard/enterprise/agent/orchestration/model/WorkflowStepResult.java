package dashboard.enterprise.agent.orchestration.model;

import java.util.List;
import java.util.Map;

public record WorkflowStepResult(
        String stepId,
        String agentId,
        String status,
        String summary,
        int attempts,
        List<String> evidence,
        Map<String, Object> output
) {
    public WorkflowStepResult {
        stepId = stepId == null ? "" : stepId;
        agentId = agentId == null ? "" : agentId;
        status = status == null ? "UNKNOWN" : status;
        summary = summary == null ? "" : summary;
        evidence = evidence == null ? List.of() : List.copyOf(evidence);
        output = output == null ? Map.of() : Map.copyOf(output);
    }
}
