package dashboard.enterprise.agent.skillstudio.registry;

import dashboard.enterprise.agent.skillstudio.model.SkillPackage;

import java.util.Collection;

public interface SkillPackageRegistry {
    void register(SkillPackage skillPackage);
    SkillPackage require(String skillId, String version);
    SkillPackage latest(String skillId);
    Collection<SkillPackage> all();
}
