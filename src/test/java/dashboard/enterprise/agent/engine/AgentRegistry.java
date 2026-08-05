package dashboard.enterprise.agent.engine;

import dashboard.enterprise.agent.model.AgentDefinition;
import java.util.Collection;

public interface AgentRegistry {
    void register(AgentDefinition agent);
    AgentDefinition require(String agentId);
    Collection<AgentDefinition> all();
}
