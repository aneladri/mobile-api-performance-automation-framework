package dashboard.enterprise.agent.connector.model;
import java.util.List;
public record ConnectorDefinition(String schemaVersion, String connectorId, String name, String provider,
                                  ConnectorMode mode, List<String> capabilityIds, List<SecretReference> secrets) {
    public ConnectorDefinition {
        schemaVersion=safe(schemaVersion); connectorId=required(connectorId,"Connector id"); name=required(name,"Connector name");
        provider=required(provider,"Provider"); mode=mode==null?ConnectorMode.REPLAY:mode;
        capabilityIds=capabilityIds==null?List.of():List.copyOf(capabilityIds); secrets=secrets==null?List.of():List.copyOf(secrets);
    }
    private static String safe(String v){return v==null?"":v.trim();}
    private static String required(String v,String label){String n=safe(v);if(n.isEmpty())throw new IllegalArgumentException(label+" is required.");return n;}
}
