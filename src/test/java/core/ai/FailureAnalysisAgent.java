package core.ai;

public class FailureAnalysisAgent implements Agent {

    private final FailureAnalysisRuntime runtime =
            new FailureAnalysisRuntime();

    @Override
    public String getName() {
        return "failure-analysis";
    }

    @Override
    public String analyze(String input) {
        AgentResponse response = runtime.analyze(input);

        AgentMetricsCollector.record(
        getName(),
        response.getConfidence(),
        "Unknown Failure".equals(response.getClassification())
        );

        return """
                Failure Analysis:
                %s

                Root Cause:
                %s

                Recommendation:
                %s

                Confidence:
                %d
                """.formatted(
                response.getClassification(),
                response.getRootCause(),
                response.getRecommendation(),
                response.getConfidence()
        );
    }
}
