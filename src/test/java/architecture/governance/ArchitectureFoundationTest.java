package architecture.governance;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ArchitectureFoundationTest {

    private static final List<String> REQUIRED_FILES = List.of(
            "docs/product/MAPAF_PRODUCT_VISION.md",
            "docs/architecture/blueprint/MAPAF_ARCHITECTURE_BLUEPRINT_V1.md",
            "docs/architecture/blueprint/MAPAF_REPOSITORY_STRATEGY.md",
            "docs/architecture/blueprint/MAPAF_CAPABILITY_OWNERSHIP.md",
            "docs/governance/ARCHITECTURE_REVIEW_CHECKLIST.md",
            "docs/governance/SPRINT_DELIVERY_STANDARD.md"
    );

    @Test
    public void shouldProvideRequiredArchitectureFoundationArtifacts() {
        for (String requiredFile : REQUIRED_FILES) {
            Path path = Path.of(requiredFile);
            Assert.assertTrue(Files.isRegularFile(path), "Missing architecture artifact: " + requiredFile);
            try {
                Assert.assertTrue(Files.size(path) > 0, "Empty architecture artifact: " + requiredFile);
            } catch (Exception exception) {
                Assert.fail("Unable to inspect architecture artifact: " + requiredFile, exception);
            }
        }
    }

    @Test
    public void shouldProvideAcceptedArchitectureDecisionRecords() throws Exception {
        Path adrDirectory = Path.of("docs/architecture/adr");
        Assert.assertTrue(Files.isDirectory(adrDirectory), "ADR directory is missing");
        long acceptedDecisions;
        try (var files = Files.list(adrDirectory)) {
            acceptedDecisions = files
                    .filter(path -> path.getFileName().toString().startsWith("ADR-"))
                    .filter(path -> {
                        try {
                            return Files.readString(path).contains("Status: Accepted");
                        } catch (Exception exception) {
                            return false;
                        }
                    })
                    .count();
        }
        Assert.assertTrue(acceptedDecisions >= 5, "Expected at least five accepted foundation ADRs");
    }
}
