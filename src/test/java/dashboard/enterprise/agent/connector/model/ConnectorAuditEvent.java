package dashboard.enterprise.agent.connector.model;
import java.util.Map;
public record ConnectorAuditEvent(String schemaVersion, String generatedAt, String correlationId, String connectorId,
                                  String capabilityId, String mode, String outcome, Map<String,String> metadata) {
    public ConnectorAuditEvent { schemaVersion=safe(schemaVersion); generatedAt=safe(generatedAt); correlationId=safe(correlationId); connectorId=safe(connectorId);
        capabilityId=safe(capabilityId); mode=safe(mode); outcome=safe(outcome); metadata=metadata==null?Map.of():Map.copyOf(metadata); }
    private static String safe(String v){return v==null?"":v.trim();}
}
