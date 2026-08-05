package dashboard.enterprise.agent.integration.source.tool;

import dashboard.enterprise.agent.model.AgentContext;
import dashboard.enterprise.agent.tool.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class PipelineTriggerTool implements AgentTool {
    @Override
    public ToolDefinition definition() {
        return new ToolDefinition(
                "mapaf.agent.tool/v1",
                "pipeline-trigger",
                "Pipeline Trigger",
                "Creates a governed replay pipeline run request.",
                PermissionLevel.WRITE_WITH_APPROVAL
        );
    }

    @Override
    public ToolResult execute(AgentContext context, ToolRequest request) {
        String suite = String.valueOf(request.parameters().getOrDefault("suite", "targeted-regression"));
        String runId = "REPLAY-" + Instant.now().toEpochMilli();
        return new ToolResult(
                true,
                "Replay pipeline triggered for suite " + suite + ".",
                List.of("pipeline-run:" + runId),
                Map.of("runId", runId, "suite", suite, "status", "QUEUED")
        );
    }
}
