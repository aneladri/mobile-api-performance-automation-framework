package core.ai;

import java.util.LinkedHashMap;
import java.util.Map;

public class AgentRegistry {

    private final Map<String, Agent> agents =
            new LinkedHashMap<>();

    public void register(Agent agent) {
        agents.put(agent.getName(), agent);
    }

    public Agent get(String name) {
        return agents.get(name);
    }

    public Map<String, Agent> getAll() {
        return agents;
    }
}
