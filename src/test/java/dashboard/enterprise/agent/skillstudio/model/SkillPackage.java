package dashboard.enterprise.agent.skillstudio.model;

import java.nio.file.Path;
import java.util.List;

public record SkillPackage(
        String schemaVersion,
        SkillManifest manifest,
        Path packageRoot,
        SkillLifecycleStatus status,
        List<Path> assets
) {
    public SkillPackage {
        schemaVersion = schemaVersion == null ? "" : schemaVersion.trim();
        if (manifest == null) throw new IllegalArgumentException("Skill manifest is required.");
        if (packageRoot == null) throw new IllegalArgumentException("Skill package root is required.");
        status = status == null ? SkillLifecycleStatus.DRAFT : status;
        assets = assets == null ? List.of() : List.copyOf(assets);
    }

    public String coordinate() {
        return manifest.skillId() + ":" + manifest.version();
    }
}
