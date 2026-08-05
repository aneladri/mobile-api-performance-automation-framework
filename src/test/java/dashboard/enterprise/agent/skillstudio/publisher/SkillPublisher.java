package dashboard.enterprise.agent.skillstudio.publisher;

import dashboard.enterprise.agent.skillstudio.model.*;
import dashboard.enterprise.agent.skillstudio.validation.SkillPackageValidator;

public final class SkillPublisher {
    private final SkillPackageValidator validator;

    public SkillPublisher(SkillPackageValidator validator) {
        this.validator = validator;
    }

    public SkillPackage publish(SkillPackage skillPackage, SkillEvaluationResult evaluation) {
        var validation = validator.validate(skillPackage);
        if (!validation.valid()) {
            throw new IllegalStateException("Skill package is invalid: " + validation.errors());
        }
        if (evaluation == null || !evaluation.passed()) {
            throw new IllegalStateException("Skill evaluation must pass before publication.");
        }
        return new SkillPackage(
                skillPackage.schemaVersion(),
                skillPackage.manifest(),
                skillPackage.packageRoot(),
                SkillLifecycleStatus.PUBLISHED,
                skillPackage.assets()
        );
    }
}
