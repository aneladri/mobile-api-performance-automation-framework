package core.ai.generation;

public class AutomationFeaturePackage {

    private final String featureName;
    private final AutomationProject project;

    public AutomationFeaturePackage(
            String featureName,
            AutomationProject project
    ) {
        this.featureName = featureName;
        this.project = project;
    }

    public String getFeatureName() {
        return featureName;
    }

    public AutomationProject getProject() {
        return project;
    }
}