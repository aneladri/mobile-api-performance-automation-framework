package core.ai;

public class PerformanceAnalysisAgent implements Agent {

    private final PerformanceAnalysisRuntime runtime =
            new PerformanceAnalysisRuntime();

    @Override
    public String getName() {
        return "performance-analysis";
    }

    @Override
    public String analyze(String input) {

        String report =
                runtime.analyze(input);

        int confidence = 90;

        if (report.contains("WARNING")) {
            confidence = 75;
        }

        if (report.contains("REGRESSION")) {
            confidence = 60;
        }

        if (report.contains("CRITICAL")) {
            confidence = 40;
        }

        AgentMetricsCollector.record(
                getName(),
                confidence,
                confidence < 50
        );

        return "Performance Impact:\n" + report;
    }
}