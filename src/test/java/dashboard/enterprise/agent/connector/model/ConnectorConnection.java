package dashboard.enterprise.agent.connector.model;
import java.util.Map;
public record ConnectorConnection(String schemaVersion, String connectionId, String endpoint, ConnectorMode mode,
                                  Map<String,String> attributes) {
    public ConnectorConnection { schemaVersion=safe(schemaVersion); connectionId=required(connectionId,"Connection id"); endpoint=safe(endpoint);
        mode=mode==null?ConnectorMode.REPLAY:mode; attributes=attributes==null?Map.of():Map.copyOf(attributes); }
    private static String safe(String v){return v==null?"":v.trim();}
    private static String required(String v,String label){String n=safe(v);if(n.isEmpty())throw new IllegalArgumentException(label+" is required.");return n;}
}
