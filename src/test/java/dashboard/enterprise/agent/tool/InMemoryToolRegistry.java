package dashboard.enterprise.agent.tool;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class InMemoryToolRegistry implements ToolRegistry {
    private final Map<String, AgentTool> tools = new LinkedHashMap<>();

    @Override
    public void register(AgentTool tool) {
        if (tool == null) {
            throw new IllegalArgumentException("Tool is required.");
        }
        String id = tool.definition().toolId();
        if (tools.putIfAbsent(id, tool) != null) {
            throw new IllegalArgumentException("Duplicate tool: " + id);
        }
    }

    @Override
    public AgentTool require(String toolId) {
        AgentTool tool = tools.get(toolId);
        if (tool == null) {
            throw new IllegalArgumentException("Unknown tool: " + toolId);
        }
        return tool;
    }

    @Override
    public Collection<AgentTool> all() {
        return ListCopy.copy(tools.values());
    }

    private static final class ListCopy {
        private static <T> Collection<T> copy(Collection<T> values) {
            return java.util.List.copyOf(values);
        }
    }
}
