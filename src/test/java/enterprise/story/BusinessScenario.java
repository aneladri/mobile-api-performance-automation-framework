package enterprise.story;

import java.util.List;

public record BusinessScenario(
        String id,
        String name,
        String businessObjective,
        List<Persona> personas,
        BusinessWorkflow workflow,
        List<String> qualityGates
) {
    public BusinessScenario {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Scenario ID must not be blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Scenario name must not be blank");
        }
        if (businessObjective == null || businessObjective.isBlank()) {
            throw new IllegalArgumentException("Business objective must not be blank");
        }
        personas = List.copyOf(personas == null ? List.of() : personas);
        if (workflow == null) {
            throw new IllegalArgumentException("Workflow must not be null");
        }
        qualityGates = List.copyOf(qualityGates == null ? List.of() : qualityGates);
    }
}
