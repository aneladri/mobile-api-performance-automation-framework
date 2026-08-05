package dashboard.enterprise.agent.orchestration.engine;

import dashboard.enterprise.agent.orchestration.model.MultiAgentWorkflow;

public interface WorkflowRegistry {
    void register(MultiAgentWorkflow workflow);
    MultiAgentWorkflow require(String workflowId);
}
