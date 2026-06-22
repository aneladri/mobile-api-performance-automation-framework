package core.ai;

public class ArchitectAnalysisAgent implements Agent {

    private final ArchitectAnalysisRuntime runtime =
            new ArchitectAnalysisRuntime();

    @Override
    public String getName() {
        return "architecture-analysis";
    }

    @Override
    public String analyze(String input) {
        return "Architecture Impact:\n" + runtime.analyze(input);
    }
}
