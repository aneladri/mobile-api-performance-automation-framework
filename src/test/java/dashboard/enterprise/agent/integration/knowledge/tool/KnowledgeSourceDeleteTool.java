package dashboard.enterprise.agent.integration.knowledge.tool;

import dashboard.enterprise.agent.model.AgentContext;
import dashboard.enterprise.agent.tool.*;

import java.util.List;
import java.util.Map;

public final class KnowledgeSourceDeleteTool implements AgentTool {
    @Override
    public ToolDefinition definition() {
        return new ToolDefinition(
                "mapaf.agent.tool/v1",
                "knowledge-source-delete",
                "Knowledge Source Delete",
                "Deletes a knowledge source. This operation is prohibited.",
                PermissionLevel.PROHIBITED
        );
    }

    @Override
    public ToolResult execute(AgentContext context, ToolRequest request) {
        return new ToolResult(false, "Knowledge source deletion is prohibited.", List.of(), Map.of());
    }
}
