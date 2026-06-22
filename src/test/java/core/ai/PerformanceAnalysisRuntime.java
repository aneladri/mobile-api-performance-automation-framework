package core.ai;

public class PerformanceAnalysisRuntime {

    public String analyze(String input) {

        if (input.contains("SSL")) {
            return "No performance impact detected";
        }

        return "Performance impact unknown";
    }
}
