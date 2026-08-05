package dashboard.enterprise.agent.skillstudio.validation;

import dashboard.enterprise.agent.skillstudio.model.SkillPackage;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public final class SkillPackageValidator {
    public SkillValidationResult validate(SkillPackage skillPackage) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        if (skillPackage == null) {
            return new SkillValidationResult(false, List.of("Skill package is required."), List.of());
        }
        if (!Files.isRegularFile(skillPackage.packageRoot().resolve("SKILL.md"))) {
            errors.add("SKILL.md is required.");
        }
        if (!Files.isRegularFile(skillPackage.packageRoot().resolve("skill.json"))) {
            errors.add("skill.json is required.");
        }
        if (skillPackage.manifest().requiredCapabilities().isEmpty()) {
            warnings.add("No required capabilities were declared.");
        }
        if (skillPackage.manifest().entrypoints().isEmpty()) {
            errors.add("At least one skill entrypoint is required.");
        }
        return new SkillValidationResult(errors.isEmpty(), errors, warnings);
    }
}
