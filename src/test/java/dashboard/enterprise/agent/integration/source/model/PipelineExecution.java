package dashboard.enterprise.agent.integration.source.model;

import java.util.List;

public record PipelineExecution(
        String schemaVersion,
        String provider,
        String pipelineId,
        String runId,
        String status,
        long durationSeconds,
        String failedStage,
        List<String> stages,
        List<String> artifacts,
        List<String> logHighlights
) {
    public PipelineExecution {
        schemaVersion = safe(schemaVersion);
        provider = safe(provider);
        pipelineId = safe(pipelineId);
        runId = safe(runId);
        status = safe(status);
        failedStage = safe(failedStage);
        stages = stages == null ? List.of() : List.copyOf(stages);
        artifacts = artifacts == null ? List.of() : List.copyOf(artifacts);
        logHighlights = logHighlights == null ? List.of() : List.copyOf(logHighlights);
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
