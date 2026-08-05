package core.enterprise.agents;

import java.time.Instant;
import java.util.List;

public record AgentAnalysisRecord(String agentName, String skillVersion, String model,
                                  double confidence, String finding, List<String> recommendations,
                                  boolean humanApprovalRequired, Instant generatedAt) {
    public AgentAnalysisRecord {
        recommendations = recommendations == null ? List.of() : List.copyOf(recommendations);
        generatedAt = generatedAt == null ? Instant.now() : generatedAt;
    }
}
