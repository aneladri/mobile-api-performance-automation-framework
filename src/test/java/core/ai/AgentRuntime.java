package core.ai;

public class AgentRuntime {

    public AgentResponse execute(AgentRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Agent request cannot be null");
        }

        if (request.getAgentType() == null || request.getAgentType().isBlank()) {
            throw new IllegalArgumentException("Agent type is required");
        }

        if (request.getInput() == null || request.getInput().isBlank()) {
            throw new IllegalArgumentException("Agent input is required");
        }

        if ("failure-analysis".equalsIgnoreCase(request.getAgentType())) {
            FailureAnalysisRuntime runtime = new FailureAnalysisRuntime();
            return runtime.analyze(request.getInput());
        }

        AgentResponse response = new AgentResponse();
        response.setClassification("Unsupported Agent Type");
        response.setRootCause("No runtime is available for agent type: " + request.getAgentType());
        response.setRecommendation("Use supported agent type: failure-analysis");
        response.setConfidence(0);

        return response;
    }
}