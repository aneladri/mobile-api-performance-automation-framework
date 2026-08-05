package dashboard.enterprise.agent.skill;

import java.util.Collection;

public interface SkillRegistry {
    void register(SkillDefinition skill);
    SkillDefinition require(String skillId);
    Collection<SkillDefinition> all();
}
