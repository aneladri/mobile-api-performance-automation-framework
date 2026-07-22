package roomscan.blueprint;

import roomscan.workflow.ScanState;

import java.util.List;

public class RoomScanBlueprint {

    private final String featureName;
    private final String userStory;
    private final List<ScanState> workflowStates;
    private final AutomationStrategy automationStrategy;
    private final List<String> uiValidations;
    private final List<String> apiValidations;
    private final List<String> performanceValidations;

    public RoomScanBlueprint(
            String featureName,
            String userStory,
            List<ScanState> workflowStates,
            AutomationStrategy automationStrategy,
            List<String> uiValidations,
            List<String> apiValidations,
            List<String> performanceValidations
    ) {
        this.featureName = featureName;
        this.userStory = userStory;
        this.workflowStates = workflowStates;
        this.automationStrategy = automationStrategy;
        this.uiValidations = uiValidations;
        this.apiValidations = apiValidations;
        this.performanceValidations = performanceValidations;
    }

    public String getFeatureName() {
        return featureName;
    }

    public String getUserStory() {
        return userStory;
    }

    public List<ScanState> getWorkflowStates() {
        return workflowStates;
    }

    public AutomationStrategy getAutomationStrategy() {
        return automationStrategy;
    }

    public List<String> getUiValidations() {
        return uiValidations;
    }

    public List<String> getApiValidations() {
        return apiValidations;
    }

    public List<String> getPerformanceValidations() {
        return performanceValidations;
    }
}
