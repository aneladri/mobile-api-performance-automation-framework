package dashboard.enterprise.agent.skillstudio.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import dashboard.enterprise.agent.skillstudio.model.*;

import java.nio.file.*;
import java.util.List;
import java.util.stream.Stream;

public final class FileSystemSkillLoader {
    private static final ObjectMapper MAPPER = JsonMapper.getInstance();

    public SkillPackage load(Path packageRoot) throws Exception {
        if (packageRoot == null || !Files.isDirectory(packageRoot)) {
            throw new IllegalArgumentException("Skill package directory is required.");
        }
        Path manifestPath = packageRoot.resolve("skill.json");
        if (!Files.isRegularFile(manifestPath)) {
            throw new IllegalArgumentException("skill.json is required.");
        }
        SkillManifest manifest = MAPPER.readValue(manifestPath.toFile(), SkillManifest.class);
        try (Stream<Path> paths = Files.walk(packageRoot)) {
            List<Path> assets = paths.filter(Files::isRegularFile).sorted().toList();
            return new SkillPackage(
                    "mapaf.skill.package/v1",
                    manifest,
                    packageRoot.toAbsolutePath().normalize(),
                    SkillLifecycleStatus.DRAFT,
                    assets
            );
        }
    }
}
