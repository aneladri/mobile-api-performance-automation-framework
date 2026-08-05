package dashboard.enterprise.agent.skillstudio.model;

import java.util.Map;

public record SkillTestCase(
        String schemaVersion,
        String testId,
        String description,
        Map<String, Object> input,
        Map<String, Object> expectedOutput
) {
    public SkillTestCase {
        schemaVersion = schemaVersion == null ? "" : schemaVersion.trim();
        testId = testId == null ? "" : testId.trim();
        description = description == null ? "" : description.trim();
        input = input == null ? Map.of() : Map.copyOf(input);
        expectedOutput = expectedOutput == null ? Map.of() : Map.copyOf(expectedOutput);
    }
}
