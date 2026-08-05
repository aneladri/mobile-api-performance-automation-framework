package dashboard.enterprise.agent.orchestration.model;

import java.util.List;
import java.util.Map;

public record WorkflowExecution(
        String schemaVersion,
        String executionId,
        String workflowId,
        WorkflowStatus status,
        String summary,
        List<WorkflowStepResult> stepResults,
        List<AgentHandoff> handoffs,
        List<String> evidence,
        Map<String, Object> context
) {
    public WorkflowExecution {
        schemaVersion = schemaVersion == null ? "" : schemaVersion;
        executionId = executionId == null ? "" : executionId;
        workflowId = workflowId == null ? "" : workflowId;
        status = status == null ? WorkflowStatus.FAILED : status;
        summary = summary == null ? "" : summary;
        stepResults = stepResults == null ? List.of() : List.copyOf(stepResults);
        handoffs = handoffs == null ? List.of() : List.copyOf(handoffs);
        evidence = evidence == null ? List.of() : List.copyOf(evidence);
        context = context == null ? Map.of() : Map.copyOf(context);
    }
}
