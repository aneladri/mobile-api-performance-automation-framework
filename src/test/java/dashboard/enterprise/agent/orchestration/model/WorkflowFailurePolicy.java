package dashboard.enterprise.agent.orchestration.model;

public enum WorkflowFailurePolicy {
    STOP_ON_FAILURE,
    CONTINUE_WITH_WARNING,
    RETRY_ONCE,
    WAIT_FOR_APPROVAL,
    SKIP_OPTIONAL_STEP
}
