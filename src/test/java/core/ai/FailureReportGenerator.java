package core.ai;

public class FailureReportGenerator
        implements ReportGenerator {

    @Override
    public String generateReport(
            AgentResponse response
    ) {

        return """
                # Failure Analysis Report

                Classification:
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
