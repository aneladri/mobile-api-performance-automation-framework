package dashboard.enterprise.agent.connector.provider;
import dashboard.enterprise.agent.connector.model.*; import dashboard.enterprise.agent.model.AgentContext;
public interface EnterpriseConnectorProvider { ConnectorDefinition definition(); ConnectorConnection connection(); int priority(); ConnectorHealth health(); ConnectorResponse invoke(AgentContext context, ConnectorRequest request) throws Exception;
 default boolean supports(String capabilityId){return definition().capabilityIds().contains(capabilityId);} }
