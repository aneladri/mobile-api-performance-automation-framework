package dashboard.enterprise.agent.orchestration.policy;

import dashboard.enterprise.agent.orchestration.model.WorkflowStep;

public final class WorkflowPolicyEvaluator {
    public void verify(WorkflowStep step, boolean approved) {
        if (step.approvalRequired() && !approved) {
            throw new SecurityException("Workflow step requires approval: " + step.stepId());
        }
    }
}
