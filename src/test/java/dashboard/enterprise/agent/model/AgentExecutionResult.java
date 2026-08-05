package dashboard.enterprise.agent.model;

import java.util.List;
import java.util.Map;

public record AgentExecutionResult(
        String schemaVersion,
        String agentId,
        String status,
        String summary,
        List<String> skillsUsed,
        List<String> toolsUsed,
        List<String> evidence,
        Map<String, Object> output
) {
    public AgentExecutionResult {
        schemaVersion = schemaVersion == null ? "" : schemaVersion;
        agentId = agentId == null ? "" : agentId;
        status = status == null ? "UNKNOWN" : status;
        summary = summary == null ? "" : summary;
        skillsUsed = skillsUsed == null ? List.of() : List.copyOf(skillsUsed);
        toolsUsed = toolsUsed == null ? List.of() : List.copyOf(toolsUsed);
        evidence = evidence == null ? List.of() : List.copyOf(evidence);
        output = output == null ? Map.of() : Map.copyOf(output);
    }
}
