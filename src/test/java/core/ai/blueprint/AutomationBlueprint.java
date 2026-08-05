package core.ai.blueprint;

import java.util.List;

public class AutomationBlueprint {

    private final String featureName;
    private final String requirement;
    private final List<ScreenBlueprint> screens;
    private final List<FlowBlueprint> flows;
    private final List<TestBlueprint> tests;

    public AutomationBlueprint(
            String featureName,
            String requirement,
            List<ScreenBlueprint> screens,
            List<FlowBlueprint> flows,
            List<TestBlueprint> tests
    ) {
        this.featureName = featureName;
        this.requirement = requirement;
        this.screens = screens;
        this.flows = flows;
        this.tests = tests;
    }

    public String getFeatureName() {
        return featureName;
    }

    public String getRequirement() {
        return requirement;
    }

    public List<ScreenBlueprint> getScreens() {
        return screens;
    }

    public List<FlowBlueprint> getFlows() {
        return flows;
    }

    public List<TestBlueprint> getTests() {
        return tests;
    }
}
