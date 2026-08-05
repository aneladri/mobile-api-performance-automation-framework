package dashboard.enterprise.agent.integration.source.tests;

import dashboard.enterprise.agent.audit.InMemoryAgentAuditStore;
import dashboard.enterprise.agent.engine.DefaultAgentEngine;
import dashboard.enterprise.agent.engine.InMemoryAgentRegistry;
import dashboard.enterprise.agent.integration.source.model.ImpactAnalysis;
import dashboard.enterprise.agent.integration.source.model.RepositoryChange;
import dashboard.enterprise.agent.integration.source.tool.*;
import dashboard.enterprise.agent.model.*;
import dashboard.enterprise.agent.policy.AgentPermissionEvaluator;
import dashboard.enterprise.agent.skill.InMemorySkillRegistry;
import dashboard.enterprise.agent.skill.SkillDefinition;
import dashboard.enterprise.agent.tool.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class SourceControlIntegrationTest {

    @Test
    public void shouldReadGithubReplayAndGenerateImpactAnalysis() throws Exception {
        Path root = Path.of(".").toAbsolutePath().normalize();
        var read = new ReplayRepositoryChangeReadTool(
                "github",
                root.resolve("src/test/resources/source-control/github/repository-change.json")
        );
        ToolResult readResult = read.execute(
                new AgentContext(root, "SC-1", "demo-user", Map.of()),
                new ToolRequest("read", Map.of())
        );
        Assert.assertTrue(readResult.successful());
        RepositoryChange change = (RepositoryChange) readResult.data().get("repositoryChange");

        ToolResult impactResult = new ImpactAnalysisTool().execute(
                new AgentContext(root, "SC-2", "demo-user", Map.of()),
                new ToolRequest("analyze", Map.of("repositoryChange", change))
        );
        ImpactAnalysis analysis = (ImpactAnalysis) impactResult.data().get("impactAnalysis");
        Assert.assertTrue(analysis.recommendedSuites().contains("api-regression"));
        Assert.assertTrue(analysis.recommendedSuites().contains("mobile-regression"));
        Assert.assertNotEquals(analysis.risk(), "LOW");
    }

    @Test
    public void shouldReadAzurePullRequestReplay() throws Exception {
        Path root = Path.of(".").toAbsolutePath().normalize();
        ToolResult result = new ReplayPullRequestReadTool(
                "azure",
                root.resolve("src/test/resources/source-control/azure/pull-request.json")
        ).execute(
                new AgentContext(root, "SC-3", "demo-user", Map.of()),
                new ToolRequest("read", Map.of())
        );
        Assert.assertTrue(result.successful());
        Assert.assertTrue(result.data().containsKey("pullRequest"));
    }

    @Test
    public void shouldReadPipelineReplay() throws Exception {
        Path root = Path.of(".").toAbsolutePath().normalize();
        ToolResult result = new ReplayPipelineStatusTool(
                root.resolve("src/test/resources/source-control/pipeline/pipeline-run.json")
        ).execute(
                new AgentContext(root, "SC-4", "demo-user", Map.of()),
                new ToolRequest("status", Map.of())
        );
        Assert.assertTrue(result.successful());
        Assert.assertTrue(result.summary().contains("PASSED"));
    }

    @Test(expectedExceptions = SecurityException.class)
    public void shouldRequireApprovalBeforeTriggeringPipeline() throws Exception {
        var agents = new InMemoryAgentRegistry();
        var skills = new InMemorySkillRegistry();
        var tools = new InMemoryToolRegistry();
        agents.register(new AgentDefinition(
                "mapaf.agent.definition/v1",
                "impact-agent",
                "Impact Agent",
                "Analyzes source changes and starts approved pipelines.",
                List.of("source-impact-analysis"),
                List.of("pipeline-trigger"),
                ApprovalPolicy.HUMAN_REQUIRED_FOR_WRITES
        ));
        skills.register(new SkillDefinition(
                "mapaf.agent.skill/v1",
                "source-impact-analysis",
                "Source Impact Analysis",
                "Run approved test pipelines based on source impact.",
                List.of("pipeline-trigger"),
                List.of("Require human approval before triggering a pipeline")
        ));
        tools.register(new PipelineTriggerTool());

        new DefaultAgentEngine(
                agents,
                skills,
                tools,
                new AgentPermissionEvaluator(),
                new InMemoryAgentAuditStore()
        ).execute(
                "impact-agent",
                "source-impact-analysis",
                "pipeline-trigger",
                new AgentContext(Path.of("."), "SC-5", "demo-user", Map.of()),
                new ToolRequest("trigger", Map.of("suite", "api-regression")),
                false
        );
    }
}
