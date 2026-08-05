package dashboard.enterprise.agent.connector.model;
import java.util.List;
public record ConnectorHealth(String schemaVersion, String connectorId, ConnectorStatus status, String summary, List<String> diagnostics) {
    public ConnectorHealth { schemaVersion=safe(schemaVersion); connectorId=safe(connectorId); status=status==null?ConnectorStatus.UNAVAILABLE:status;
        summary=safe(summary); diagnostics=diagnostics==null?List.of():List.copyOf(diagnostics); }
    private static String safe(String v){return v==null?"":v.trim();}
}
