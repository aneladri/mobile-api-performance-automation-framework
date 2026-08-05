package dashboard.enterprise.agent.runtime.mcp;

import java.util.List;
import java.util.Map;

public record McpServerDefinition(
        String schemaVersion,
        String serverId,
        String transport,
        String endpoint,
        List<String> capabilities,
        Map<String, String> metadata
) {
    public McpServerDefinition {
        schemaVersion = schemaVersion == null ? "" : schemaVersion;
        serverId = serverId == null ? "" : serverId.trim();
        transport = transport == null ? "stdio" : transport.trim();
        endpoint = endpoint == null ? "" : endpoint.trim();
        capabilities = capabilities == null ? List.of() : List.copyOf(capabilities);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
        if (serverId.isEmpty()) throw new IllegalArgumentException("MCP server id is required.");
    }
}
