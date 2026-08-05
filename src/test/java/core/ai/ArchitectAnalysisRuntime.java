package core.ai;

public class ArchitectAnalysisRuntime {

    public String analyze(String input) {

        if (input.contains("SSL")) {
            return "No architecture changes required";
        }

        return "Architecture review not required";
    }
}
