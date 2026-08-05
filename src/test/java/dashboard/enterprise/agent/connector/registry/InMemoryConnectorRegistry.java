package dashboard.enterprise.agent.connector.registry;
import dashboard.enterprise.agent.connector.provider.EnterpriseConnectorProvider; import java.util.*;
public final class InMemoryConnectorRegistry implements ConnectorRegistry { private final Map<String,EnterpriseConnectorProvider> values=new LinkedHashMap<>();
 public void register(EnterpriseConnectorProvider p){if(p==null)throw new IllegalArgumentException("Connector is required.");if(values.putIfAbsent(p.definition().connectorId(),p)!=null)throw new IllegalArgumentException("Duplicate connector: "+p.definition().connectorId());}
 public EnterpriseConnectorProvider require(String id){EnterpriseConnectorProvider p=values.get(id);if(p==null)throw new IllegalArgumentException("Unknown connector: "+id);return p;} public Collection<EnterpriseConnectorProvider> all(){return List.copyOf(values.values());}}
