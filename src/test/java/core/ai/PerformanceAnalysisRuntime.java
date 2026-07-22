package core.ai;

public class PerformanceAnalysisRuntime {

    private final PerformanceRegressionAnalyzer analyzer =
            new PerformanceRegressionAnalyzer();

    public String analyze(
            PerformanceBaseline baseline,
            double currentP95Ms,
            double currentErrorRate,
            double currentThroughput
    ) {
        return analyzer.generateReport(
                baseline,
                currentP95Ms,
                currentErrorRate,
                currentThroughput
        );
    }

    public String analyze(String input) {
        PerformanceBaseline baseline =
                new PerformanceBaseline(40.46, 0.00, 2.98);

        return analyze(baseline, 70.00, 0.00, 2.90);
    }
}