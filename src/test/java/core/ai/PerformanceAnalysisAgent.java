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
        return "Performance Impact:\n" + runtime.analyze(input);
    }
}