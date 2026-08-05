package dashboard.enterprise.agent.integration.work.tool;

import dashboard.enterprise.agent.model.AgentContext;
import dashboard.enterprise.agent.tool.*;
import java.util.List;
import java.util.Map;

public final class DraftTestTaskCreateTool implements AgentTool {
    private final String provider;
    public DraftTestTaskCreateTool(String provider) { this.provider = provider; }
    @Override public ToolDefinition definition() {
        String id = provider.equalsIgnoreCase("jira") ? "jira-draft-test-task-create" : "azure-draft-test-task-create";
        return new ToolDefinition("mapaf.agent.tool/v1", id, id,
                "Creates draft test tasks. Replay mode returns simulated identifiers.", PermissionLevel.WRITE_WITH_APPROVAL);
    }
    @Override public ToolResult execute(AgentContext context, ToolRequest request) {
        String prefix = provider.equalsIgnoreCase("jira") ? "JIRA-DRAFT" : "ADO-DRAFT";
        return new ToolResult(true, "Draft test tasks created in replay mode.",
                List.of("approval=" + request.parameters().getOrDefault("approvedBy", "unknown")),
                Map.of("draftIds", List.of(prefix + "-1001", prefix + "-1002"), "provider", provider));
    }
}
