package core.ai;

import core.ai.providers.AIResponse;
import core.ai.services.PerformanceAIService;

public class PerformanceAnalysisAgent implements Agent {

    private final PerformanceAnalysisRuntime runtime =
            new PerformanceAnalysisRuntime();

    private final PerformanceAIService service =
            new PerformanceAIService();

    @Override
    public String getName() {
        return "performance-analysis";
    }

    @Override
    public String analyze(String input) {

        String report =
                runtime.analyze(input);

        int confidence =
                calculateConfidence(report);

        boolean needsAI =
                confidence < 50
                        || report.contains("REGRESSION")
                        || report.contains("CRITICAL");

        AgentMetricsCollector.record(
                getName(),
                confidence,
                needsAI
        );

        if (needsAI) {
            AIResponse response =
                    service.analyze(input);

            if (response.isSuccessful()) {
                return response.getContent();
            }
        }

        return """
                Performance Impact:
                %s
                """.formatted(
                report
        );
    }

    private int calculateConfidence(String report) {

        int confidence =
                90;

        if (report.contains("WARNING")) {
            confidence = 75;
        }

        if (report.contains("REGRESSION")) {
            confidence = 60;
        }

        if (report.contains("CRITICAL")) {
            confidence = 40;
        }

        return confidence;
    }
}