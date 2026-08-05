package dashboard.enterprise.agent.skillstudio.validation;

import java.util.List;

public record SkillValidationResult(boolean valid, List<String> errors, List<String> warnings) {
    public SkillValidationResult {
        errors = errors == null ? List.of() : List.copyOf(errors);
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
    }
}
