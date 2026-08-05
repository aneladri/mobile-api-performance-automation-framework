package dashboard.enterprise.agent.skillstudio.registry;

import dashboard.enterprise.agent.skillstudio.model.SkillPackage;

import java.util.*;

public final class InMemorySkillPackageRegistry implements SkillPackageRegistry {
    private final Map<String, SkillPackage> packages = new LinkedHashMap<>();

    @Override
    public void register(SkillPackage skillPackage) {
        if (skillPackage == null) throw new IllegalArgumentException("Skill package is required.");
        if (packages.putIfAbsent(skillPackage.coordinate(), skillPackage) != null) {
            throw new IllegalArgumentException("Duplicate skill package: " + skillPackage.coordinate());
        }
    }

    @Override
    public SkillPackage require(String skillId, String version) {
        SkillPackage value = packages.get(skillId + ":" + version);
        if (value == null) throw new IllegalArgumentException("Unknown skill package: " + skillId + ":" + version);
        return value;
    }

    @Override
    public SkillPackage latest(String skillId) {
        return packages.values().stream()
                .filter(value -> value.manifest().skillId().equals(skillId))
                .max(Comparator.comparing(value -> value.manifest().version()))
                .orElseThrow(() -> new IllegalArgumentException("Unknown skill: " + skillId));
    }

    @Override
    public Collection<SkillPackage> all() {
        return List.copyOf(packages.values());
    }
}
