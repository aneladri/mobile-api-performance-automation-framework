package dashboard.enterprise.agent.integration.source.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import dashboard.enterprise.agent.integration.source.model.PipelineExecution;
import dashboard.enterprise.agent.model.AgentContext;
import dashboard.enterprise.agent.tool.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class ReplayPipelineStatusTool implements AgentTool {
    private static final ObjectMapper MAPPER = JsonMapper.getInstance();
    private final Path fixture;

    public ReplayPipelineStatusTool(Path fixture) {
        this.fixture = fixture;
    }

    @Override
    public ToolDefinition definition() {
        return new ToolDefinition(
                "mapaf.agent.tool/v1",
                "pipeline-status-read",
                "Pipeline Status Read",
                "Reads pipeline status and evidence in replay mode.",
                PermissionLevel.READ_ONLY
        );
    }

    @Override
    public ToolResult execute(AgentContext context, ToolRequest request) throws Exception {
        JsonNode n = MAPPER.readTree(fixture.toFile());
        PipelineExecution execution = new PipelineExecution(
                "mapaf.pipeline.execution/v1",
                n.path("provider").asText(),
                n.path("pipelineId").asText(),
                n.path("runId").asText(),
                n.path("status").asText(),
                n.path("durationSeconds").asLong(),
                n.path("failedStage").asText(),
                MAPPER.convertValue(n.path("stages"), MAPPER.getTypeFactory().constructCollectionType(List.class, String.class)),
                MAPPER.convertValue(n.path("artifacts"), MAPPER.getTypeFactory().constructCollectionType(List.class, String.class)),
                MAPPER.convertValue(n.path("logHighlights"), MAPPER.getTypeFactory().constructCollectionType(List.class, String.class))
        );
        return new ToolResult(true, "Pipeline run " + execution.runId() + " is " + execution.status() + ".", List.of(fixture.toString()), Map.of("pipelineExecution", execution));
    }
}
