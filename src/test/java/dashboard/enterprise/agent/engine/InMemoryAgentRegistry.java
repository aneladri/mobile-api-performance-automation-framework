package dashboard.enterprise.agent.engine;

import dashboard.enterprise.agent.model.AgentDefinition;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class InMemoryAgentRegistry implements AgentRegistry {
    private final Map<String, AgentDefinition> agents = new LinkedHashMap<>();

    @Override
    public void register(AgentDefinition agent) {
        if (agents.putIfAbsent(agent.agentId(), agent) != null) {
            throw new IllegalArgumentException("Duplicate agent: " + agent.agentId());
        }
    }

    @Override
    public AgentDefinition require(String agentId) {
        AgentDefinition agent = agents.get(agentId);
        if (agent == null) {
            throw new IllegalArgumentException("Unknown agent: " + agentId);
        }
        return agent;
    }

    @Override
    public Collection<AgentDefinition> all() {
        return List.copyOf(agents.values());
    }
}
