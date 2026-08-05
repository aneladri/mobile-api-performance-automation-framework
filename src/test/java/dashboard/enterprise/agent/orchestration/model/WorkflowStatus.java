package dashboard.enterprise.agent.orchestration.model;

public enum WorkflowStatus {
    PENDING,
    RUNNING,
    WAITING_FOR_APPROVAL,
    PASSED,
    PASSED_WITH_WARNINGS,
    FAILED
}
