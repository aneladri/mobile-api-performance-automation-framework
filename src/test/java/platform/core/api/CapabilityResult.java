package platform.core.api;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import platform.core.execution.ExecutionStatus;

/** Normalized result returned by every platform capability. */
public record CapabilityResult(
        ExecutionStatus status,
        String summary,
        Map<String, Object> outputs
) {
    public CapabilityResult {
        status = Objects.requireNonNull(status, "status");
        summary = summary == null ? "" : summary;
        outputs = outputs == null
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(outputs));
    }

    public static CapabilityResult passed(String summary) {
        return new CapabilityResult(ExecutionStatus.PASSED, summary, Map.of());
    }

    public static CapabilityResult failed(String summary) {
        return new CapabilityResult(ExecutionStatus.FAILED, summary, Map.of());
    }
}
