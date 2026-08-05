package dashboard.enterprise.agent.integration.work.model;

import java.util.List;

public record TestDesign(
        String schemaVersion,
        String workItemId,
        String workItemKey,
        List<TestScenario> scenarios
) {
    public TestDesign {
        schemaVersion = schemaVersion == null ? "" : schemaVersion;
        workItemId = workItemId == null ? "" : workItemId;
        workItemKey = workItemKey == null ? "" : workItemKey;
        scenarios = scenarios == null ? List.of() : List.copyOf(scenarios);
    }
}
