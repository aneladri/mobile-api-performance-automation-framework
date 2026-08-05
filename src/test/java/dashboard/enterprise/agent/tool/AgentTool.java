package dashboard.enterprise.agent.tool;

import dashboard.enterprise.agent.model.AgentContext;

public interface AgentTool {
    ToolDefinition definition();
    ToolResult execute(AgentContext context, ToolRequest request) throws Exception;
}
