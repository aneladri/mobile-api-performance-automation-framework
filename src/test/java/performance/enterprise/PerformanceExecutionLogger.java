package performance.enterprise;

import java.util.Locale;

public final class PerformanceExecutionLogger {

    public void print(EnterprisePerformanceMetrics m) {
        line('=');
        title("MAPAF PERFORMANCE ENGINEERING PLATFORM");
        line('=');
        field("Execution ID", m.executionId);
        field("Scenario", m.scenario);
        field("Environment", m.environment);
        field("Engines", "k6 + JMeter");
        field("Framework", "MAPAF Enterprise");
        line('=');

        section("Execution Profile");
        field("Profile", "Smoke");
        field("Virtual Users", Integer.toString(m.virtualUsers));
        field("JMeter Threads", Integer.toString(m.jmeterThreads));
        field("Duration", m.durationSeconds + " sec");

        section("Engine Results");
        for (EnterprisePerformanceMetrics.EngineMetrics engine : m.engines.values()) {
            System.out.printf(Locale.ROOT,
                    "%-10s Requests: %-6d  Avg: %-8.2f ms  P95: %-8.2f ms  Errors: %.2f%%%n",
                    engine.engine,
                    engine.requests,
                    engine.averageMs,
                    engine.p95Ms,
                    engine.errorRatePercent);
        }

        section("Unified Performance Metrics");
        field("Requests", Integer.toString(m.requests));
        field("Passed / Failed", m.passed + " / " + m.failed);
        field("Availability", format(m.availabilityPercent, "%"));
        field("Error Rate", format(m.errorRatePercent, "%"));
        field("Throughput", format(m.throughputPerSecond, " req/sec"));
        field("Average", format(m.averageMs, " ms"));
        field("Median / P50", format(m.medianMs, " ms"));
        field("P90", format(m.p90Ms, " ms"));
        field("P95", format(m.p95Ms, " ms"));
        field("P99", format(m.p99Ms, " ms"));
        field("Minimum", format(m.minimumMs, " ms"));
        field("Maximum", format(m.maximumMs, " ms"));
        field("Data Received", humanBytes(m.bytesReceived));
        field("Data Sent", humanBytes(m.bytesSent));

        section("Quality Gate");
        gate("Availability >= 99%", m.availabilityPercent >= 99.0);
        gate("P95 < 500 ms", m.p95Ms < 500.0);
        gate("Error Rate < 1%", m.errorRatePercent < 1.0);
        field("Overall", m.qualityGate);

        section("Performance Diagnostics");
        field("Finding", m.bottleneck);
        field("Confidence", m.confidencePercent + "%");
        field("Recommendation", m.recommendation);

        line('=');
        field("Overall Result", m.qualityGate);
        line('=');
    }

    private void section(String name) {
        System.out.println();
        line('-');
        System.out.println(name);
        line('-');
    }

    private void gate(String name, boolean passed) {
        System.out.printf("%-28s : %s%n", name, passed ? "PASS" : "FAIL");
    }

    private void field(String name, String value) {
        System.out.printf("%-28s : %s%n", name, value);
    }

    private String format(double value, String suffix) {
        return String.format(Locale.ROOT, "%.2f%s", value, suffix);
    }

    private String humanBytes(long bytes) {
        if (bytes < 1024) return bytes + " bytes";
        if (bytes < 1024L * 1024L) return String.format(Locale.ROOT, "%.2f KB", bytes / 1024.0);
        return String.format(Locale.ROOT, "%.2f MB", bytes / (1024.0 * 1024.0));
    }

    private void title(String title) {
        System.out.println("        " + title);
    }

    private void line(char character) {
        System.out.println(String.valueOf(character).repeat(70));
    }
}
