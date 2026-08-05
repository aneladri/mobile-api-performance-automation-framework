package dashboard.enterprise.agent.tool;

import java.util.Map;

public record ToolRequest(String action, Map<String, Object> parameters) {
    public ToolRequest {
        action = action == null ? "" : action.trim();
        parameters = parameters == null ? Map.of() : Map.copyOf(parameters);
    }
}
