package dashboard.enterprise.agent.engine;

import dashboard.enterprise.agent.audit.AgentAuditEvent;
import dashboard.enterprise.agent.audit.AgentAuditStore;
import dashboard.enterprise.agent.model.AgentContext;
import dashboard.enterprise.agent.model.AgentDefinition;
import dashboard.enterprise.agent.model.AgentExecutionResult;
import dashboard.enterprise.agent.policy.AgentPermissionEvaluator;
import dashboard.enterprise.agent.skill.SkillDefinition;
import dashboard.enterprise.agent.skill.SkillRegistry;
import dashboard.enterprise.agent.tool.AgentTool;
import dashboard.enterprise.agent.tool.ToolRegistry;
import dashboard.enterprise.agent.tool.ToolRequest;
import dashboard.enterprise.agent.tool.ToolResult;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class DefaultAgentEngine {
    private final AgentRegistry agents;
    private final SkillRegistry skills;
    private final ToolRegistry tools;
    private final AgentPermissionEvaluator permissions;
    private final AgentAuditStore audit;

    public DefaultAgentEngine(
            AgentRegistry agents,
            SkillRegistry skills,
            ToolRegistry tools,
            AgentPermissionEvaluator permissions,
            AgentAuditStore audit
    ) {
        this.agents = agents;
        this.skills = skills;
        this.tools = tools;
        this.permissions = permissions;
        this.audit = audit;
    }

    public AgentExecutionResult execute(
            String agentId,
            String skillId,
            String toolId,
            AgentContext context,
            ToolRequest request,
            boolean approved
    ) throws Exception {
        AgentDefinition agent = agents.require(agentId);
        SkillDefinition skill = skills.require(skillId);
        AgentTool tool = tools.require(toolId);

        if (!agent.skillIds().contains(skillId)) {
            throw new SecurityException("Skill is not assigned to agent: " + skillId);
        }
        if (!skill.requiredToolIds().contains(toolId)) {
            throw new SecurityException("Skill does not permit tool: " + toolId);
        }

        permissions.verify(agent, tool.definition(), approved);
        ToolResult result = tool.execute(context, request);

        audit.append(new AgentAuditEvent(
                "mapaf.agent.audit/v1",
                Instant.now().toString(),
                context.correlationId(),
                agentId,
                skillId,
                toolId,
                result.successful() ? "SUCCESS" : "FAILED",
                Map.of("actor", context.actor())
        ));

        return new AgentExecutionResult(
                "mapaf.agent.execution/v1",
                agentId,
                result.successful() ? "SUCCESS" : "FAILED",
                result.summary(),
                List.of(skillId),
                List.of(toolId),
                result.evidence(),
                result.data()
        );
    }
}
