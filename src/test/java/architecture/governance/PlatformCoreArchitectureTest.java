package architecture.governance;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PlatformCoreArchitectureTest {

    private static final List<String> REQUIRED_ARTIFACTS = List.of(
            "docs/architecture/blueprint/MAPAF_PLATFORM_CORE_ARCHITECTURE_V1.md",
            "docs/architecture/blueprint/MAPAF_CORE_DOMAIN_MODEL.md",
            "docs/architecture/blueprint/MAPAF_EVENT_CATALOG.md",
            "docs/architecture/blueprint/MAPAF_PLATFORM_CONTRACT_CATALOG.md",
            "docs/architecture/blueprint/MAPAF_PLATFORM_CORE_MIGRATION_PLAN.md",
            "docs/architecture/diagrams/MAPAF_PLATFORM_CORE_DIAGRAMS.md"
    );

    @Test
    public void shouldProvidePlatformCoreArchitectureArtifacts() throws Exception {
        for (String artifact : REQUIRED_ARTIFACTS) {
            Path path = Path.of(artifact);
            Assert.assertTrue(Files.isRegularFile(path), "Missing platform-core artifact: " + artifact);
            Assert.assertTrue(Files.size(path) > 100, "Platform-core artifact is unexpectedly small: " + artifact);
        }
    }

    @Test
    public void shouldProvideAcceptedPlatformCoreAdrs() throws Exception {
        Path adrDirectory = Path.of("docs/architecture/adr");
        try (var files = Files.list(adrDirectory)) {
            long count = files
                    .filter(path -> path.getFileName().toString().matches("ADR-01[2-6].*\\.md"))
                    .filter(path -> {
                        try {
                            return Files.readString(path).contains("Status: Accepted");
                        } catch (Exception ignored) {
                            return false;
                        }
                    })
                    .count();
            Assert.assertEquals(count, 5L, "Expected five accepted Sprint 0.2 ADRs");
        }
    }
}
