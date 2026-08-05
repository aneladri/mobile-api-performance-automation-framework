package core.enterprise.agents;

public record AgentGovernancePolicy(double minimumConfidence, boolean requireHumanApproval,
                                    boolean failExecutionOnAgentError, boolean allowPermanentCodeChanges,
                                    int maximumRecommendations) {
    public AgentGovernancePolicy {
        if (minimumConfidence < 0.0 || minimumConfidence > 1.0) {
            throw new IllegalArgumentException("Minimum confidence must be between 0 and 1");
        }
        if (maximumRecommendations <= 0) {
            throw new IllegalArgumentException("Maximum recommendations must be greater than zero");
        }
    }
    public static AgentGovernancePolicy demoPolicy() {
        return new AgentGovernancePolicy(0.75, true, false, false, 5);
    }
}
