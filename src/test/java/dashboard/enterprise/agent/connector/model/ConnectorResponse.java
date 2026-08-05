package dashboard.enterprise.agent.connector.model;
import java.util.List; import java.util.Map;
public record ConnectorResponse(String schemaVersion, String requestId, String connectorId, boolean successful, String summary,
                                List<String> evidence, Map<String,Object> data) {
    public ConnectorResponse { schemaVersion=safe(schemaVersion); requestId=safe(requestId); connectorId=safe(connectorId); summary=safe(summary);
        evidence=evidence==null?List.of():List.copyOf(evidence); data=data==null?Map.of():Map.copyOf(data); }
    private static String safe(String v){return v==null?"":v.trim();}
}
