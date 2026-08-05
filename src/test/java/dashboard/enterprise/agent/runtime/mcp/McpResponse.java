package dashboard.enterprise.agent.runtime.mcp;

import java.util.Map;

public record McpResponse(String schemaVersion, String requestId, boolean successful, String summary, Map<String, Object> result) {
    public McpResponse {
        schemaVersion = schemaVersion == null ? "" : schemaVersion;
        requestId = requestId == null ? "" : requestId;
        summary = summary == null ? "" : summary;
        result = result == null ? Map.of() : Map.copyOf(result);
    }
}
