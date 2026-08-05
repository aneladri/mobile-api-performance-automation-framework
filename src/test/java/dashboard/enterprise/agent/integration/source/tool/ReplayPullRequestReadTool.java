package dashboard.enterprise.agent.integration.source.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import dashboard.enterprise.agent.integration.source.model.PullRequest;
import dashboard.enterprise.agent.model.AgentContext;
import dashboard.enterprise.agent.tool.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class ReplayPullRequestReadTool implements AgentTool {
    private static final ObjectMapper MAPPER = JsonMapper.getInstance();
    private final String provider;
    private final Path fixture;

    public ReplayPullRequestReadTool(String provider, Path fixture) {
        this.provider = provider;
        this.fixture = fixture;
    }

    @Override
    public ToolDefinition definition() {
        return new ToolDefinition(
                "mapaf.agent.tool/v1",
                provider.toLowerCase() + "-pull-request-read",
                provider + " Pull Request Read",
                "Reads pull request evidence in replay mode.",
                PermissionLevel.READ_ONLY
        );
    }

    @Override
    public ToolResult execute(AgentContext context, ToolRequest request) throws Exception {
        JsonNode n = MAPPER.readTree(fixture.toFile());
        PullRequest pr = new PullRequest(
                "mapaf.pull-request/v1",
                n.path("provider").asText(provider),
                n.path("id").asText(),
                n.path("title").asText(),
                n.path("description").asText(),
                n.path("sourceBranch").asText(),
                n.path("targetBranch").asText(),
                n.path("status").asText(),
                n.path("mergeable").asBoolean(),
                MAPPER.convertValue(n.path("reviewers"), MAPPER.getTypeFactory().constructCollectionType(List.class, String.class)),
                MAPPER.convertValue(n.path("changedFiles"), MAPPER.getTypeFactory().constructCollectionType(List.class, String.class))
        );
        return new ToolResult(true, "Read pull request " + pr.id() + ".", List.of(fixture.toString()), Map.of("pullRequest", pr));
    }
}
