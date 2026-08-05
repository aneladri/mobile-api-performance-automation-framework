package dashboard.enterprise.agent.integration.work.skill;

import dashboard.enterprise.agent.integration.work.model.*;
import java.util.ArrayList;
import java.util.List;

public final class DeterministicTestDesignSkill {
    public TestDesign generate(WorkItem item) {
        List<TestScenario> scenarios = new ArrayList<>();
        scenarios.add(new TestScenario("FUNCTIONAL", "Complete primary story flow", item.title(),
                List.of("Open the feature", "Complete the primary action", "Save the result"),
                "The story acceptance criteria are satisfied.", "HIGH"));
        scenarios.add(new TestScenario("MOBILE", "Validate mobile interaction", item.title(),
                List.of("Launch the mobile app", "Complete the flow on a supported device"),
                "The flow completes without UI or device errors.", "HIGH"));
        scenarios.add(new TestScenario("API", "Validate service contract", item.title(),
                List.of("Send a valid API request", "Validate status and response schema"),
                "The API returns the expected contract.", "HIGH"));
        scenarios.add(new TestScenario("PERFORMANCE", "Validate response SLA", item.title(),
                List.of("Run the business transaction", "Capture p95 latency and error rate"),
                "Configured performance thresholds pass.", "MEDIUM"));
        scenarios.add(new TestScenario("ACCESSIBILITY", "Validate accessibility basics", item.title(),
                List.of("Scan the page", "Check labels, focus order, and contrast"),
                "No critical accessibility violation is found.", "MEDIUM"));
        scenarios.add(new TestScenario("NEGATIVE", "Reject invalid input", item.title(),
                List.of("Submit invalid or incomplete data"),
                "The system rejects the request with a clear message.", "HIGH"));
        return new TestDesign("mapaf.test-design/v1", item.id(), item.key(), scenarios);
    }
}
