package dashboard.enterprise.agent.connector.resolution;
import dashboard.enterprise.agent.connector.provider.EnterpriseConnectorProvider; import dashboard.enterprise.agent.connector.registry.ConnectorRegistry;
import java.util.Comparator;
public final class ConnectorResolver { private final ConnectorRegistry registry; public ConnectorResolver(ConnectorRegistry registry){this.registry=registry;}
 public EnterpriseConnectorProvider resolve(String capabilityId,String preferredConnectorId){ if(preferredConnectorId!=null&&!preferredConnectorId.isBlank()){EnterpriseConnectorProvider p=registry.require(preferredConnectorId);if(!p.supports(capabilityId))throw new IllegalArgumentException("Preferred connector does not support capability: "+capabilityId);return p;}
 return registry.all().stream().filter(p->p.supports(capabilityId)).sorted(Comparator.comparingInt(EnterpriseConnectorProvider::priority).reversed().thenComparing(p->p.definition().connectorId())).findFirst().orElseThrow(()->new IllegalArgumentException("No connector supports capability: "+capabilityId));}}
