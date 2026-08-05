package dashboard.enterprise.agent.audit;

import java.util.List;

public interface AgentAuditStore {
    void append(AgentAuditEvent event);
    List<AgentAuditEvent> events();
}
