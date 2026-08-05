package dashboard.enterprise.agent.tests;

import dashboard.enterprise.agent.audit.InMemoryAgentAuditStore;
import dashboard.enterprise.agent.engine.DefaultAgentEngine;
import dashboard.enterprise.agent.engine.InMemoryAgentRegistry;
import dashboard.enterprise.agent.model.AgentContext;
import dashboard.enterprise.agent.model.AgentDefinition;
import dashboard.enterprise.agent.model.ApprovalPolicy;
import dashboard.enterprise.agent.policy.AgentPermissionEvaluator;
import dashboard.enterprise.agent.skill.InMemorySkillRegistry;
import dashboard.enterprise.agent.skill.SkillDefinition;
import dashboard.enterprise.agent.tool.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class AgentFoundationTest {

    @Test
    public void shouldExecuteAllowedReadOnlyToolAndAudit() throws Exception {
        var agents = new InMemoryAgentRegistry();
        var skills = new InMemorySkillRegistry();
        var tools = new InMemoryToolRegistry();
        var audit = new InMemoryAgentAuditStore();

        agents.register(new AgentDefinition(
                "mapaf.agent.definition/v1",
                "release-advisor",
                "Release Advisor",
                "Advises on release readiness.",
                List.of("release-advisor"),
                List.of("forecast-intelligence-read"),
                ApprovalPolicy.READ_ONLY
        ));

        skills.register(new SkillDefinition(
                "mapaf.agent.skill/v1",
                "release-advisor",
                "Release Advisor",
                "Use forecast evidence to advise on release risk.",
                List.of("forecast-intelligence-read"),
                List.of("Read the forecast report", "Return evidence")
        ));

        tools.register(new AgentTool() {
            @Override
            public ToolDefinition definition() {
                return new ToolDefinition(
                        "mapaf.agent.tool/v1",
                        "forecast-intelligence-read",
                        "Forecast Intelligence Read",
                        "Reads forecast evidence.",
                        PermissionLevel.READ_ONLY
                );
            }

            @Override
            public ToolResult execute(AgentContext context, ToolRequest request) {
                return new ToolResult(
                        true,
                        "Release risk is LOW.",
                        List.of("forecast-report.json"),
                        Map.of("releaseRisk", "LOW")
                );
            }
        });

        var engine = new DefaultAgentEngine(
                agents,
                skills,
                tools,
                new AgentPermissionEvaluator(),
                audit
        );

        var result = engine.execute(
                "release-advisor",
                "release-advisor",
                "forecast-intelligence-read",
                new AgentContext(Path.of("."), "CORR-1", "demo-user", Map.of()),
                new ToolRequest("read", Map.of()),
                false
        );

        Assert.assertEquals(result.status(), "SUCCESS");
        Assert.assertEquals(result.output().get("releaseRisk"), "LOW");
        Assert.assertEquals(audit.events().size(), 1);
    }

    @Test(expectedExceptions = SecurityException.class)
    public void shouldRejectUnapprovedWriteTool() throws Exception {
        var agents = new InMemoryAgentRegistry();
        var skills = new InMemorySkillRegistry();
        var tools = new InMemoryToolRegistry();

        agents.register(new AgentDefinition(
                "mapaf.agent.definition/v1",
                "test-agent",
                "Test Agent",
                "Writes draft tests.",
                List.of("draft-tests"),
                List.of("jira-create-draft"),
                ApprovalPolicy.HUMAN_REQUIRED_FOR_WRITES
        ));
        skills.register(new SkillDefinition(
                "mapaf.agent.skill/v1",
                "draft-tests",
                "Draft Tests",
                "Create draft test tasks.",
                List.of("jira-create-draft"),
                List.of()
        ));
        tools.register(new AgentTool() {
            public ToolDefinition definition() {
                return new ToolDefinition(
                        "mapaf.agent.tool/v1",
                        "jira-create-draft",
                        "Jira Create Draft",
                        "Creates a draft Jira item.",
                        PermissionLevel.WRITE_WITH_APPROVAL
                );
            }
            public ToolResult execute(AgentContext context, ToolRequest request) {
                return new ToolResult(true, "created", List.of(), Map.of());
            }
        });

        new DefaultAgentEngine(
                agents,
                skills,
                tools,
                new AgentPermissionEvaluator(),
                new InMemoryAgentAuditStore()
        ).execute(
                "test-agent",
                "draft-tests",
                "jira-create-draft",
                new AgentContext(Path.of("."), "CORR-2", "demo-user", Map.of()),
                new ToolRequest("create", Map.of()),
                false
        );
    }
}
