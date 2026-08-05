package dashboard.enterprise.agent.orchestration.tests;

import dashboard.enterprise.agent.audit.InMemoryAgentAuditStore;
import dashboard.enterprise.agent.engine.*;
import dashboard.enterprise.agent.model.*;
import dashboard.enterprise.agent.orchestration.engine.*;
import dashboard.enterprise.agent.orchestration.evidence.WorkflowEvidencePublisher;
import dashboard.enterprise.agent.orchestration.model.*;
import dashboard.enterprise.agent.orchestration.policy.WorkflowPolicyEvaluator;
import dashboard.enterprise.agent.policy.AgentPermissionEvaluator;
import dashboard.enterprise.agent.skill.*;
import dashboard.enterprise.agent.tool.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class MultiAgentOrchestrationTest {

    @Test
    public void shouldRunGovernedMultiAgentWorkflow() throws Exception {
        var agents = new InMemoryAgentRegistry();
        var skills = new InMemorySkillRegistry();
        var tools = new InMemoryToolRegistry();
        var audit = new InMemoryAgentAuditStore();

        register(agents, skills, tools, "work-item-agent", "work-item-read", "work-item-read", PermissionLevel.READ_ONLY, Map.of("workItem", "RS-101"));
        register(agents, skills, tools, "knowledge-agent", "knowledge-search", "knowledge-search", PermissionLevel.READ_ONLY, Map.of("knowledge", "upload retry guidance"));
        register(agents, skills, tools, "test-design-agent", "test-design", "test-design-generate", PermissionLevel.AUTONOMOUS_WRITE, Map.of("testDesign", List.of("api", "mobile", "performance")));
        register(agents, skills, tools, "impact-agent", "impact-analysis", "impact-analysis", PermissionLevel.READ_ONLY, Map.of("recommendedSuites", List.of("api-regression", "mobile-regression")));
        register(agents, skills, tools, "pipeline-agent", "pipeline-trigger", "pipeline-trigger", PermissionLevel.WRITE_WITH_APPROVAL, Map.of("pipelineStatus", "PASSED"));
        register(agents, skills, tools, "release-advisor", "release-advice", "release-advice", PermissionLevel.READ_ONLY, Map.of("recommendation", "READY"));

        var engine = new DefaultAgentEngine(agents, skills, tools, new AgentPermissionEvaluator(), audit);
        var registry = new InMemoryWorkflowRegistry();
        registry.register(roomScanWorkflow());
        var orchestrator = new WorkflowOrchestrator(registry, engine, new WorkflowPolicyEvaluator());

        var execution = orchestrator.execute(
                "roomscan-delivery",
                "WF-1001",
                new AgentContext(Path.of("."), "CORR-WF-1", "demo-user", Map.of()),
                Map.of("trigger-pipeline", true)
        );

        Assert.assertEquals(execution.status(), WorkflowStatus.PASSED);
        Assert.assertEquals(execution.stepResults().size(), 6);
        Assert.assertEquals(execution.handoffs().size(), 6);
        Assert.assertEquals(execution.context().get("recommendation"), "READY");
        Assert.assertEquals(audit.events().size(), 6);
    }

    @Test
    public void shouldPauseForApproval() throws Exception {
        var setup = minimalPipelineSetup();
        var execution = setup.orchestrator.execute(
                "approval-flow",
                "WF-1002",
                new AgentContext(Path.of("."), "CORR-WF-2", "demo-user", Map.of()),
                Map.of()
        );
        Assert.assertEquals(execution.status(), WorkflowStatus.WAITING_FOR_APPROVAL);
        Assert.assertTrue(execution.summary().contains("requires approval"));
    }

    @Test
    public void shouldPublishWorkflowEvidence() throws Exception {
        Path root = Files.createTempDirectory("mapaf-workflow-evidence");
        WorkflowExecution execution = new WorkflowExecution(
                "mapaf.agent.workflow-execution/v1", "WF-EVIDENCE", "demo",
                WorkflowStatus.PASSED, "done", List.of(), List.of(), List.of("evidence"), Map.of("risk", "LOW")
        );
        Path file = WorkflowEvidencePublisher.publish(root, execution);
        Assert.assertTrue(Files.isRegularFile(file));
        Assert.assertTrue(Files.readString(file).contains("mapaf.agent.workflow-execution/v1"));
    }

    private static MultiAgentWorkflow roomScanWorkflow() {
        return new MultiAgentWorkflow(
                "mapaf.agent.workflow/v1", "roomscan-delivery", "RoomScan Delivery Workflow", "Governed delivery workflow",
                List.of(
                        step("read-work-item", "work-item-agent", "work-item-read", "work-item-read", false),
                        step("search-knowledge", "knowledge-agent", "knowledge-search", "knowledge-search", false),
                        step("generate-tests", "test-design-agent", "test-design", "test-design-generate", false),
                        step("analyze-impact", "impact-agent", "impact-analysis", "impact-analysis", false),
                        step("trigger-pipeline", "pipeline-agent", "pipeline-trigger", "pipeline-trigger", true),
                        step("release-advice", "release-advisor", "release-advice", "release-advice", false)
                )
        );
    }

    private static WorkflowStep step(String id, String agent, String skill, String tool, boolean approval) {
        return new WorkflowStep(
                "mapaf.agent.workflow-step/v1", id, id, agent, skill, tool,
                approval ? WorkflowFailurePolicy.WAIT_FOR_APPROVAL : WorkflowFailurePolicy.STOP_ON_FAILURE,
                approval, false, Map.of()
        );
    }

    private static void register(
            InMemoryAgentRegistry agents,
            InMemorySkillRegistry skills,
            InMemoryToolRegistry tools,
            String agentId,
            String skillId,
            String toolId,
            PermissionLevel permission,
            Map<String, Object> output
    ) {
        agents.register(new AgentDefinition(
                "mapaf.agent.definition/v1", agentId, agentId, agentId,
                List.of(skillId), List.of(toolId),
                permission == PermissionLevel.WRITE_WITH_APPROVAL ? ApprovalPolicy.HUMAN_REQUIRED_FOR_WRITES : ApprovalPolicy.AUTONOMOUS_WITHIN_POLICY
        ));
        skills.register(new SkillDefinition(
                "mapaf.agent.skill/v1", skillId, skillId, skillId, List.of(toolId), List.of("Execute governed step")
        ));
        tools.register(new AgentTool() {
            public ToolDefinition definition() {
                return new ToolDefinition("mapaf.agent.tool/v1", toolId, toolId, toolId, permission);
            }
            public ToolResult execute(AgentContext context, ToolRequest request) {
                return new ToolResult(true, toolId + " completed", List.of(toolId + ".json"), output);
            }
        });
    }

    private static Setup minimalPipelineSetup() {
        var agents = new InMemoryAgentRegistry();
        var skills = new InMemorySkillRegistry();
        var tools = new InMemoryToolRegistry();
        register(agents, skills, tools, "pipeline-agent", "pipeline-trigger", "pipeline-trigger", PermissionLevel.WRITE_WITH_APPROVAL, Map.of("pipeline", "started"));
        var engine = new DefaultAgentEngine(agents, skills, tools, new AgentPermissionEvaluator(), new InMemoryAgentAuditStore());
        var registry = new InMemoryWorkflowRegistry();
        registry.register(new MultiAgentWorkflow(
                "mapaf.agent.workflow/v1", "approval-flow", "Approval Flow", "approval test",
                List.of(step("trigger-pipeline", "pipeline-agent", "pipeline-trigger", "pipeline-trigger", true))
        ));
        return new Setup(new WorkflowOrchestrator(registry, engine, new WorkflowPolicyEvaluator()));
    }

    private record Setup(WorkflowOrchestrator orchestrator) {}
}
