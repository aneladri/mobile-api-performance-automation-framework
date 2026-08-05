package dashboard.enterprise.agent.orchestration.model;

import java.util.Map;

public record AgentHandoff(
        String schemaVersion,
        String fromAgentId,
        String toAgentId,
        String stepId,
        Map<String, Object> payload
) {
    public AgentHandoff {
        schemaVersion = schemaVersion == null ? "" : schemaVersion;
        fromAgentId = fromAgentId == null ? "" : fromAgentId;
        toAgentId = toAgentId == null ? "" : toAgentId;
        stepId = stepId == null ? "" : stepId;
        payload = payload == null ? Map.of() : Map.copyOf(payload);
    }
}
