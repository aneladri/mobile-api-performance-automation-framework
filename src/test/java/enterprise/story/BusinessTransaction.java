package enterprise.story;

import java.util.List;
import java.util.Objects;

public record BusinessTransaction(
        String id,
        String name,
        WorkflowStage stage,
        Persona actor,
        String objective,
        List<String> expectedOutcomes,
        List<String> risks
) {
    public BusinessTransaction {
        requireText(id, "Transaction ID");
        requireText(name, "Transaction name");
        Objects.requireNonNull(stage, "Workflow stage must not be null");
        Objects.requireNonNull(actor, "Actor must not be null");
        requireText(objective, "Objective");
        expectedOutcomes = List.copyOf(expectedOutcomes == null ? List.of() : expectedOutcomes);
        risks = List.copyOf(risks == null ? List.of() : risks);
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
    }
}
