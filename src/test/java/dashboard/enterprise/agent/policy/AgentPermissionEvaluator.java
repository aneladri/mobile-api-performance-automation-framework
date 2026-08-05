package dashboard.enterprise.agent.policy;

import dashboard.enterprise.agent.model.AgentDefinition;
import dashboard.enterprise.agent.tool.PermissionLevel;
import dashboard.enterprise.agent.tool.ToolDefinition;

public final class AgentPermissionEvaluator {
    public void verify(AgentDefinition agent, ToolDefinition tool, boolean approved) {
        if (!agent.allowedToolIds().contains(tool.toolId())) {
            throw new SecurityException("Agent is not allowed to use tool: " + tool.toolId());
        }
        if (tool.permissionLevel() == PermissionLevel.PROHIBITED) {
            throw new SecurityException("Tool is prohibited: " + tool.toolId());
        }
        if (tool.permissionLevel() == PermissionLevel.WRITE_WITH_APPROVAL && !approved) {
            throw new SecurityException("Human approval required for tool: " + tool.toolId());
        }
    }
}
