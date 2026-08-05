package dashboard.enterprise.agent.tool;

import java.util.Collection;

public interface ToolRegistry {
    void register(AgentTool tool);
    AgentTool require(String toolId);
    Collection<AgentTool> all();
}
