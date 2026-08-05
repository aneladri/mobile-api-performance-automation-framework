package core.enterprise.execution;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public final class EnterpriseExecutionContext {
    private final String module;
    private final String scenario;
    private final String executionId;
    private final String correlationId;
    private final String traceId;
    private final Instant startedAt;
    private final Map<String, Object> attributes = new LinkedHashMap<>();

    public EnterpriseExecutionContext(String module, String scenario, String executionId,
                                      String correlationId, String traceId) {
        this.module = requireText(module, "Module");
        this.scenario = requireText(scenario, "Scenario");
        this.executionId = requireText(executionId, "Execution ID");
        this.correlationId = requireText(correlationId, "Correlation ID");
        this.traceId = requireText(traceId, "Trace ID");
        this.startedAt = Instant.now();
    }

    public void addAttribute(String name, Object value) {
        attributes.put(requireText(name, "Attribute name"), value);
    }

    public String getModule() { return module; }
    public String getScenario() { return scenario; }
    public String getExecutionId() { return executionId; }
    public String getCorrelationId() { return correlationId; }
    public String getTraceId() { return traceId; }
    public Instant getStartedAt() { return startedAt; }
    public Map<String, Object> getAttributes() { return new LinkedHashMap<>(attributes); }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value;
    }
}
