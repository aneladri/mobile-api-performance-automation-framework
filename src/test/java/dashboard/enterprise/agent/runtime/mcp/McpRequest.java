package dashboard.enterprise.agent.runtime.mcp;

import java.util.Map;

public record McpRequest(String schemaVersion, String requestId, String method, Map<String, Object> parameters) {
    public McpRequest {
        schemaVersion = schemaVersion == null ? "" : schemaVersion;
        requestId = requestId == null ? "" : requestId;
        method = method == null ? "" : method;
        parameters = parameters == null ? Map.of() : Map.copyOf(parameters);
    }
}
