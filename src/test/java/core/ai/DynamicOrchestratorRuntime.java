package core.ai;

public class DynamicOrchestratorRuntime {

    private final AgentRegistry registry =
            new AgentRegistry();

    public DynamicOrchestratorRuntime() {

        AgentConfiguration config =
            new AgentConfiguration();

        if (config.isFailureAnalysisEnabled()) {
            registry.register(new FailureAnalysisAgent());
        }

        if (config.isDocumentationAnalysisEnabled()) {
            registry.register(new DocumentationAnalysisAgent());
        }

        if (config.isArchitectureAnalysisEnabled()) {
        registry.register(new ArchitectAnalysisAgent());
        }

        if (config.isPerformanceAnalysisEnabled()) {
            registry.register(new PerformanceAnalysisAgent());
        }
        registry.register(new HealingAnalysisAgent());
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
