package core.ai;

public class OrchestratorRuntime {

    public String analyze(String log) {

        FailureAnalysisRuntime failureRuntime =
                new FailureAnalysisRuntime();

        DocumentationAnalysisRuntime documentationRuntime =
                new DocumentationAnalysisRuntime();

        ArchitectAnalysisRuntime architectRuntime =
                new ArchitectAnalysisRuntime();

        PerformanceAnalysisRuntime performanceRuntime =
                new PerformanceAnalysisRuntime();

        AgentResponse response =
                failureRuntime.analyze(log);

        return """
                Failure Analysis:
                %s

                Root Cause:
                %s

                Recommendation:
                %s

                Documentation Impact:
                %s

                Architecture Impact:
                %s

                Performance Impact:
                %s

                Confidence:
                %d
                """.formatted(
                response.getClassification(),
                response.getRootCause(),
                response.getRecommendation(),
                documentationRuntime.analyze(log),
                architectRuntime.analyze(log),
                performanceRuntime.analyze(log),
                response.getConfidence()
        );
    }
}
