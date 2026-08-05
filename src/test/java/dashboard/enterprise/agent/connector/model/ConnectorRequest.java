package dashboard.enterprise.agent.connector.model;
import java.util.Map;
public record ConnectorRequest(String schemaVersion, String requestId, String capabilityId, Map<String,Object> parameters) {
    public ConnectorRequest { schemaVersion=safe(schemaVersion); requestId=required(requestId,"Request id"); capabilityId=required(capabilityId,"Capability id"); parameters=parameters==null?Map.of():Map.copyOf(parameters); }
    private static String safe(String v){return v==null?"":v.trim();}
    private static String required(String v,String label){String n=safe(v);if(n.isEmpty())throw new IllegalArgumentException(label+" is required.");return n;}
}
