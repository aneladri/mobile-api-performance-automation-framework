package dashboard.enterprise.agent.audit;

import java.util.Map;

public record AgentAuditEvent(
        String schemaVersion,
        String occurredAt,
        String correlationId,
        String agentId,
        String skillId,
        String toolId,
        String outcome,
        Map<String, Object> metadata
) {
    public AgentAuditEvent {
        schemaVersion = schemaVersion == null ? "" : schemaVersion;
        occurredAt = occurredAt == null ? "" : occurredAt;
        correlationId = correlationId == null ? "" : correlationId;
        agentId = agentId == null ? "" : agentId;
        skillId = skillId == null ? "" : skillId;
        toolId = toolId == null ? "" : toolId;
        outcome = outcome == null ? "UNKNOWN" : outcome;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
