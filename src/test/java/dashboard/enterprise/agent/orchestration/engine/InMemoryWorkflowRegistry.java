package dashboard.enterprise.agent.orchestration.engine;

import dashboard.enterprise.agent.orchestration.model.MultiAgentWorkflow;
import java.util.LinkedHashMap;
import java.util.Map;

public final class InMemoryWorkflowRegistry implements WorkflowRegistry {
    private final Map<String, MultiAgentWorkflow> workflows = new LinkedHashMap<>();
    public void register(MultiAgentWorkflow workflow) {
        if (workflows.putIfAbsent(workflow.workflowId(), workflow) != null) {
            throw new IllegalArgumentException("Duplicate workflow: " + workflow.workflowId());
        }
    }
    public MultiAgentWorkflow require(String workflowId) {
        MultiAgentWorkflow workflow = workflows.get(workflowId);
        if (workflow == null) throw new IllegalArgumentException("Unknown workflow: " + workflowId);
        return workflow;
    }
}
