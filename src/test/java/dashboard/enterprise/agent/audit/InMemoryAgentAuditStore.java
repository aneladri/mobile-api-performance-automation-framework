package dashboard.enterprise.agent.audit;

import java.util.ArrayList;
import java.util.List;

public final class InMemoryAgentAuditStore implements AgentAuditStore {
    private final List<AgentAuditEvent> events = new ArrayList<>();

    @Override
    public void append(AgentAuditEvent event) {
        events.add(event);
    }

    @Override
    public List<AgentAuditEvent> events() {
        return List.copyOf(events);
    }
}
