package performance.models;

public class PerformanceScenario {

    private final PerformanceScenarioType type;
    private final String name;
    private final String testPlan;
    private final String description;

    public PerformanceScenario(
            PerformanceScenarioType type,
            String name,
            String testPlan,
            String description
    ) {
        this.type = type;
        this.name = name;
        this.testPlan = testPlan;
        this.description = description;
    }

    public PerformanceScenarioType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getTestPlan() {
        return testPlan;
    }

    public String getDescription() {
        return description;
    }
}
