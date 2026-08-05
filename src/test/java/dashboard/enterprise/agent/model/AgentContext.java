package dashboard.enterprise.agent.model;

import java.nio.file.Path;
import java.util.Map;

public record AgentContext(
        Path repositoryRoot,
        String correlationId,
        String actor,
        Map<String, Object> attributes
) {
    public AgentContext {
        if (repositoryRoot == null) {
            throw new IllegalArgumentException("Repository root is required.");
        }
        correlationId = correlationId == null ? "" : correlationId.trim();
        actor = actor == null ? "unknown" : actor.trim();
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
