package core.ai;

import java.util.HashMap;
import java.util.Map;

public final class AgentMetricsCollector {

    private static final Map<String, AgentMetrics> METRICS =
            new HashMap<>();

    private AgentMetricsCollector() {
    }

    public static void record(
            String agentName,
            int confidence,
            boolean unknown
    ) {
        METRICS
                .computeIfAbsent(agentName, key -> new AgentMetrics())
                .recordRun(confidence, unknown);
    }

    public static AgentMetrics getMetrics(String agentName) {
        return METRICS.get(agentName);
    }

    public static void reset() {
        METRICS.clear();
    }
}
