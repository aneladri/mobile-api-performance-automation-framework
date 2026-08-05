package dashboard.enterprise.agent.integration.work.tool;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import dashboard.enterprise.agent.integration.work.azure.AzureBoardsWorkItemMapper;
import dashboard.enterprise.agent.integration.work.jira.JiraWorkItemMapper;
import dashboard.enterprise.agent.integration.work.model.WorkItem;
import dashboard.enterprise.agent.model.AgentContext;
import dashboard.enterprise.agent.tool.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class ReplayWorkItemReadTool implements AgentTool {
    private static final ObjectMapper MAPPER = JsonMapper.getInstance();
    private final String provider;

    public ReplayWorkItemReadTool(String provider) { this.provider = provider; }

    @Override public ToolDefinition definition() {
        String id = provider.equalsIgnoreCase("jira") ? "jira-work-item-read" : "azure-work-item-read";
        return new ToolDefinition("mapaf.agent.tool/v1", id, id, "Reads a work item in replay mode.", PermissionLevel.READ_ONLY);
    }

    @Override public ToolResult execute(AgentContext context, ToolRequest request) throws Exception {
        String fixture = provider.equalsIgnoreCase("jira")
                ? "src/test/resources/work-management/jira/roomscan-story.json"
                : "src/test/resources/work-management/azure/roomscan-story.json";
        Path path = context.repositoryRoot().resolve(fixture);
        Map<String, Object> payload = MAPPER.readValue(path.toFile(), new TypeReference<>() {});
        WorkItem item = provider.equalsIgnoreCase("jira")
                ? new JiraWorkItemMapper().map(payload)
                : new AzureBoardsWorkItemMapper().map(payload);
        return new ToolResult(true, "Work item loaded from " + provider + " replay fixture.", List.of(path.toString()), Map.of("workItem", item));
    }
}
