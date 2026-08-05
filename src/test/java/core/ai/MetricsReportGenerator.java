package core.ai;

public class MetricsReportGenerator {

    public String generate(String agentName, AgentMetrics metrics) {

        if (metrics == null) {
            return """
                    # AI Metrics Report

                    Agent:
                    %s

                    No metrics available.
                    """.formatted(agentName);
        }

        return """
                # AI Metrics Report

                Agent:
                %s

                Total Runs:
                %d

                Successful Runs:
                %d

                Unknown Classifications:
                %d

                Average Confidence:
                %d
                """.formatted(
                agentName,
                metrics.getTotalRuns(),
                metrics.getSuccessfulRuns(),
                metrics.getUnknownClassifications(),
                metrics.getAverageConfidence()
        );
    }
}
