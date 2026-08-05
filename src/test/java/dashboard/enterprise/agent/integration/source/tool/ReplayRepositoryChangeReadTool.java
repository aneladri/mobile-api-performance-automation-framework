package dashboard.enterprise.agent.integration.source.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import dashboard.enterprise.agent.integration.source.model.RepositoryChange;
import dashboard.enterprise.agent.model.AgentContext;
import dashboard.enterprise.agent.tool.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class ReplayRepositoryChangeReadTool implements AgentTool {
    private static final ObjectMapper MAPPER = JsonMapper.getInstance();
    private final String provider;
    private final Path fixture;

    public ReplayRepositoryChangeReadTool(String provider, Path fixture) {
        this.provider = provider;
        this.fixture = fixture;
    }

    @Override
    public ToolDefinition definition() {
        return new ToolDefinition(
                "mapaf.agent.tool/v1",
                provider.toLowerCase() + "-repository-change-read",
                provider + " Repository Change Read",
                "Reads repository change evidence in replay mode.",
                PermissionLevel.READ_ONLY
        );
    }

    @Override
    public ToolResult execute(AgentContext context, ToolRequest request) throws Exception {
        JsonNode n = MAPPER.readTree(fixture.toFile());
        RepositoryChange change = new RepositoryChange(
                "mapaf.repository.change/v1",
                n.path("provider").asText(provider),
                n.path("repository").asText(),
                n.path("branch").asText(),
                n.path("commitId").asText(),
                n.path("author").asText(),
                n.path("filesChanged").asInt(),
                n.path("linesAdded").asInt(),
                n.path("linesRemoved").asInt(),
                MAPPER.convertValue(n.path("changedFiles"), MAPPER.getTypeFactory().constructCollectionType(List.class, String.class)),
                n.path("generatedAt").asText()
        );
        return new ToolResult(
                true,
                "Read " + change.filesChanged() + " changed files from " + provider + ".",
                List.of(fixture.toString()),
                Map.of("repositoryChange", change)
        );
    }
}
