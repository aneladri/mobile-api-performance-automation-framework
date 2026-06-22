package core.ai;

public class DynamicOrchestratorRuntime {

    private final AgentRegistry registry =
            new AgentRegistry();

    public DynamicOrchestratorRuntime() {
        registry.register(new FailureAnalysisAgent());
        registry.register(new DocumentationAnalysisAgent());
        registry.register(new ArchitectAnalysisAgent());
        registry.register(new PerformanceAnalysisAgent());
    }

    public String analyze(String input) {
        StringBuilder report =
                new StringBuilder();

        registry.getAll()
                .values()
                .forEach(agent -> {
                    report.append(agent.analyze(input));
                    report.append(System.lineSeparator());
                    report.append(System.lineSeparator());
                });

        return report.toString();
    }
}
