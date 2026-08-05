package dashboard.enterprise.agent.integration.work.model;

import java.util.List;

public record TestScenario(
        String category,
        String title,
        String objective,
        List<String> steps,
        String expectedResult,
        String priority
) {
    public TestScenario {
        category = safe(category);
        title = safe(title);
        objective = safe(objective);
        steps = steps == null ? List.of() : List.copyOf(steps);
        expectedResult = safe(expectedResult);
        priority = safe(priority);
    }
    private static String safe(String value) { return value == null ? "" : value.trim(); }
}
