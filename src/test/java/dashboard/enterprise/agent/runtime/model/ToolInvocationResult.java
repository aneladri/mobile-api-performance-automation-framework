package dashboard.enterprise.agent.runtime.model;

import java.util.List;
import java.util.Map;

public record ToolInvocationResult(
        String schemaVersion,
        String providerId,
        String capabilityId,
        boolean successful,
        String summary,
        List<String> evidence,
        Map<String, Object> data
) {
    public ToolInvocationResult {
        schemaVersion = schemaVersion == null ? "" : schemaVersion;
        providerId = providerId == null ? "" : providerId;
        capabilityId = capabilityId == null ? "" : capabilityId;
        summary = summary == null ? "" : summary;
        evidence = evidence == null ? List.of() : List.copyOf(evidence);
        data = data == null ? Map.of() : Map.copyOf(data);
    }
}
