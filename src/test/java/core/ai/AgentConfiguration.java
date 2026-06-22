package core.ai;

public class AgentConfiguration {

    public boolean isFailureAnalysisEnabled() {
        return Boolean.parseBoolean(
                System.getProperty("ai.failure.enabled", "true")
        );
    }

    public boolean isDocumentationAnalysisEnabled() {
        return Boolean.parseBoolean(
                System.getProperty("ai.documentation.enabled", "true")
        );
    }

    public boolean isArchitectureAnalysisEnabled() {
        return Boolean.parseBoolean(
                System.getProperty("ai.architecture.enabled", "true")
        );
    }

    public boolean isPerformanceAnalysisEnabled() {
        return Boolean.parseBoolean(
                System.getProperty("ai.performance.enabled", "true")
        );
    }
}
