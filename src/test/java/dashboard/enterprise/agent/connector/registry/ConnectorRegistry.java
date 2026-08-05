package dashboard.enterprise.agent.connector.registry;
import dashboard.enterprise.agent.connector.provider.EnterpriseConnectorProvider; import java.util.Collection;
public interface ConnectorRegistry { void register(EnterpriseConnectorProvider provider); EnterpriseConnectorProvider require(String connectorId); Collection<EnterpriseConnectorProvider> all(); }
