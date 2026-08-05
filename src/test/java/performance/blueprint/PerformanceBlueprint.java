package performance.blueprint;

import performance.models.PerformanceProfile;
import performance.models.PerformanceScenario;

import java.util.List;

public class PerformanceBlueprint {

    private final String name;
    private final String source;
    private final List<PerformanceScenario> scenarios;
    private final List<PerformanceProfile> profiles;
    private final List<String> slas;
    private final List<String> risks;

    public PerformanceBlueprint(
            String name,
            String source,
            List<PerformanceScenario> scenarios,
            List<PerformanceProfile> profiles,
            List<String> slas,
            List<String> risks
    ) {
        this.name = name;
        this.source = source;
        this.scenarios = scenarios;
        this.profiles = profiles;
        this.slas = slas;
        this.risks = risks;
    }

    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }

    public List<PerformanceScenario> getScenarios() {
        return scenarios;
    }

    public List<PerformanceProfile> getProfiles() {
        return profiles;
    }

    public List<String> getSlas() {
        return slas;
    }

    public List<String> getRisks() {
        return risks;
    }
}
