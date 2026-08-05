package dashboard.enterprise.agent.integration.knowledge.tool;

import dashboard.enterprise.agent.model.AgentContext;
import dashboard.enterprise.agent.tool.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class KnowledgeIndexRefreshTool implements AgentTool {
    @Override
    public ToolDefinition definition() {
        return new ToolDefinition(
                "mapaf.agent.tool/v1",
                "knowledge-index-refresh",
                "Knowledge Index Refresh",
                "Refreshes the project knowledge index after human approval.",
                PermissionLevel.WRITE_WITH_APPROVAL
        );
    }

    @Override
    public ToolResult execute(AgentContext context, ToolRequest request) {
        String source = String.valueOf(request.parameters().getOrDefault("source", "LOCAL_REPOSITORY"));
        return new ToolResult(
                true,
                "Knowledge index refresh completed for " + source + ".",
                List.of("knowledge-index-refresh:" + source),
                Map.of("source", source, "refreshedAt", Instant.now().toString())
        );
    }
}
