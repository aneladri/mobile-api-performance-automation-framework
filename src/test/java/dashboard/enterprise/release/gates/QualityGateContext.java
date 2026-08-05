package dashboard.enterprise.release.gates;

import com.fasterxml.jackson.databind.JsonNode;

import java.nio.file.Path;
import java.util.Map;

/**
 * Read-only evidence context supplied to gate evaluators.
 */
public record QualityGateContext(
        Path repositoryRoot,
        JsonNode executiveSummary,
        Map<String, JsonNode> capabilitySummaries,
        Map<String, JsonNode> failureIntelligence,
        Map<String, Boolean> evidenceAvailability,
        Map<String, Object> attributes
) {

    public QualityGateContext {
        if (repositoryRoot == null) {
            throw new IllegalArgumentException(
                    "Repository root is required."
            );
        }

        capabilitySummaries = capabilitySummaries == null
                ? Map.of()
                : Map.copyOf(capabilitySummaries);

        failureIntelligence = failureIntelligence == null
                ? Map.of()
                : Map.copyOf(failureIntelligence);

        evidenceAvailability = evidenceAvailability == null
                ? Map.of()
                : Map.copyOf(evidenceAvailability);

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }

    public JsonNode capability(String key) {
        return capabilitySummaries.get(key);
    }

    public JsonNode intelligence(String key) {
        return failureIntelligence.get(key);
    }

    public boolean evidenceAvailable(String key) {
        return evidenceAvailability.getOrDefault(key, false);
    }
}
