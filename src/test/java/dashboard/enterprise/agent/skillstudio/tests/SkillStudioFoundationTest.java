package dashboard.enterprise.agent.skillstudio.tests;

import dashboard.enterprise.agent.skillstudio.evaluation.DeterministicSkillEvaluator;
import dashboard.enterprise.agent.skillstudio.loader.FileSystemSkillLoader;
import dashboard.enterprise.agent.skillstudio.model.*;
import dashboard.enterprise.agent.skillstudio.publisher.SkillPublisher;
import dashboard.enterprise.agent.skillstudio.registry.InMemorySkillPackageRegistry;
import dashboard.enterprise.agent.skillstudio.validation.SkillPackageValidator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class SkillStudioFoundationTest {
    private static final Path PACKAGE_ROOT = Path.of("src/test/resources/skill-studio/test-design");

    @Test
    public void shouldLoadAndValidateSkillPackage() throws Exception {
        SkillPackage skillPackage = new FileSystemSkillLoader().load(PACKAGE_ROOT);
        var result = new SkillPackageValidator().validate(skillPackage);
        Assert.assertTrue(result.valid(), String.valueOf(result.errors()));
        Assert.assertEquals(skillPackage.manifest().skillId(), "test-design");
        Assert.assertTrue(skillPackage.manifest().requiredCapabilities().contains("work-item.read"));
    }

    @Test
    public void shouldVersionAndResolveLatestSkill() throws Exception {
        SkillPackage first = new FileSystemSkillLoader().load(PACKAGE_ROOT);
        SkillManifest secondManifest = new SkillManifest(
                "mapaf.skill.manifest/v1", "test-design", "Test Design", "Versioned test design skill",
                "1.1.0", first.manifest().requiredCapabilities(), first.manifest().entrypoints(), Map.of()
        );
        SkillPackage second = new SkillPackage(
                "mapaf.skill.package/v1", secondManifest, first.packageRoot(), SkillLifecycleStatus.VALIDATED, first.assets()
        );
        var registry = new InMemorySkillPackageRegistry();
        registry.register(first);
        registry.register(second);
        Assert.assertEquals(registry.latest("test-design").manifest().version(), "1.1.0");
    }

    @Test
    public void shouldEvaluateAndPublishPassingSkill() throws Exception {
        SkillPackage skillPackage = new FileSystemSkillLoader().load(PACKAGE_ROOT);
        var testCase = new SkillTestCase(
                "mapaf.skill.test/v1", "TC-1", "Generates all required quality domains",
                Map.of("storyId", "RS-101"), Map.of("scenarioCount", 6)
        );
        SkillEvaluationResult evaluation = new DeterministicSkillEvaluator().evaluate(
                skillPackage, List.of(testCase), Map.of("scenarioCount", 6)
        );
        SkillPackage published = new SkillPublisher(new SkillPackageValidator()).publish(skillPackage, evaluation);
        Assert.assertTrue(evaluation.passed());
        Assert.assertEquals(published.status(), SkillLifecycleStatus.PUBLISHED);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void shouldRejectPublicationWhenEvaluationFails() throws Exception {
        SkillPackage skillPackage = new FileSystemSkillLoader().load(PACKAGE_ROOT);
        var evaluation = new DeterministicSkillEvaluator().evaluate(
                skillPackage,
                List.of(new SkillTestCase("mapaf.skill.test/v1", "TC-FAIL", "Failure", Map.of(), Map.of("scenarioCount", 6))),
                Map.of("scenarioCount", 2)
        );
        new SkillPublisher(new SkillPackageValidator()).publish(skillPackage, evaluation);
    }
}
