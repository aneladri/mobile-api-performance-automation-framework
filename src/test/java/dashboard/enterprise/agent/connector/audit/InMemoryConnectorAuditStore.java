package dashboard.enterprise.agent.connector.audit;
import dashboard.enterprise.agent.connector.model.ConnectorAuditEvent; import java.util.ArrayList; import java.util.List;
public final class InMemoryConnectorAuditStore implements ConnectorAuditStore { private final List<ConnectorAuditEvent> events=new ArrayList<>(); public void append(ConnectorAuditEvent event){events.add(event);} public List<ConnectorAuditEvent> events(){return List.copyOf(events);} }
