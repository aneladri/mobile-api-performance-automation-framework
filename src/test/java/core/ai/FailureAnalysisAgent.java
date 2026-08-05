package core.ai;

import core.ai.providers.AIResponse;
import core.ai.services.FailureAnalysisService;

public class FailureAnalysisAgent implements Agent {

    private final FailureAnalysisRuntime runtime =
            new FailureAnalysisRuntime();

    private final FailureAnalysisService service =
            new FailureAnalysisService();

    @Override
    public String getName() {
        return "failure-analysis";
    }

    @Override
    public String analyze(String input) {
        AgentResponse response =
                runtime.analyze(input);

        boolean unknownFailure =
                "Unknown Failure".equals(
                        response.getClassification()
                );

        AgentMetricsCollector.record(
                getName(),
                response.getConfidence(),
                unknownFailure
        );

        if (unknownFailure) {
            AIResponse aiResponse =
                    service.analyze(input);

            if (aiResponse.isSuccessful()) {
                return aiResponse.getContent();
            }
        }

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