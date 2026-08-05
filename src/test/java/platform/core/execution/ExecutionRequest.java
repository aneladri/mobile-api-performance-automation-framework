package platform.core.execution;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Immutable request submitted to the MAPAF execution engine. */
public record ExecutionRequest(
        String capabilityId,
        String scenario,
        String environment,
        Map<String, Object> parameters
) {
    public ExecutionRequest {
        capabilityId = requireText(capabilityId, "capabilityId");
        scenario = requireText(scenario, "scenario");
        environment = requireText(environment, "environment");
        parameters = parameters == null
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(parameters));
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.trim();
    }
}
