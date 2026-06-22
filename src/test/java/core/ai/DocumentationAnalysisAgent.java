package core.ai;

public class DocumentationAnalysisAgent implements Agent {

    private final DocumentationAnalysisRuntime runtime =
            new DocumentationAnalysisRuntime();

    @Override
    public String getName() {
        return "documentation-analysis";
    }

    @Override
    public String analyze(String input) {
        return "Documentation Impact:\n" + runtime.analyze(input);
    }
}
