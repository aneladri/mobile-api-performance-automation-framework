package dashboard.enterprise.agent.skill;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class InMemorySkillRegistry implements SkillRegistry {
    private final Map<String, SkillDefinition> skills = new LinkedHashMap<>();

    @Override
    public void register(SkillDefinition skill) {
        if (skill == null) {
            throw new IllegalArgumentException("Skill is required.");
        }
        if (skills.putIfAbsent(skill.skillId(), skill) != null) {
            throw new IllegalArgumentException("Duplicate skill: " + skill.skillId());
        }
    }

    @Override
    public SkillDefinition require(String skillId) {
        SkillDefinition skill = skills.get(skillId);
        if (skill == null) {
            throw new IllegalArgumentException("Unknown skill: " + skillId);
        }
        return skill;
    }

    @Override
    public Collection<SkillDefinition> all() {
        return List.copyOf(skills.values());
    }
}
