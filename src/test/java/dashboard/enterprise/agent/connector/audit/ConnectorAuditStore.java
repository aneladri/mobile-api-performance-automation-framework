package dashboard.enterprise.agent.connector.audit;
import dashboard.enterprise.agent.connector.model.ConnectorAuditEvent; import java.util.List;
public interface ConnectorAuditStore { void append(ConnectorAuditEvent event); List<ConnectorAuditEvent> events(); }
