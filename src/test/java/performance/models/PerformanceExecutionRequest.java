package performance.models;

import java.util.Map;

public class PerformanceExecutionRequest {

    private final String testPlan;
    private final String environment;
    private final String scenario;
    private final int virtualUsers;
    private final int rampUpSeconds;
    private final int durationSeconds;
    private final Map<String, String> properties;

    public PerformanceExecutionRequest(
            String testPlan,
            String environment,
            String scenario,
            int virtualUsers,
            int rampUpSeconds,
            int durationSeconds,
            Map<String, String> properties
    ) {
        this.testPlan = testPlan;
        this.environment = environment;
        this.scenario = scenario;
        this.virtualUsers = virtualUsers;
        this.rampUpSeconds = rampUpSeconds;
        this.durationSeconds = durationSeconds;
        this.properties = properties;
    }

    public String getTestPlan() {
        return testPlan;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getScenario() {
        return scenario;
    }

    public int getVirtualUsers() {
        return virtualUsers;
    }

    public int getRampUpSeconds() {
        return rampUpSeconds;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}
