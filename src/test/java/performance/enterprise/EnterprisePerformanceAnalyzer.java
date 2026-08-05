package performance.enterprise;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EnterprisePerformanceAnalyzer {

    private final ObjectMapper mapper = new ObjectMapper();

    public EnterprisePerformanceMetrics analyze(Path projectDirectory) throws IOException {
        EnterprisePerformanceMetrics result = new EnterprisePerformanceMetrics();
        result.executionId = "PERF-" + java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")
                .format(java.time.LocalDateTime.now());
        result.scenario = "Inspection Service Performance Validation";
        result.environment = System.getProperty("mapaf.environment", "QA").toUpperCase();

        Path k6File = projectDirectory.resolve("performance/results/k6/smoke-summary.json");
        Path jmeterFile = projectDirectory.resolve("performance/jmeter/results/smoke.jtl");

        EnterprisePerformanceMetrics.EngineMetrics k6 = parseK6(k6File);
        EnterprisePerformanceMetrics.EngineMetrics jmeter = parseJMeter(jmeterFile);
        result.engines.put("k6", k6);
        result.engines.put("JMeter", jmeter);

        result.requests = k6.requests + jmeter.requests;
        result.passed = k6.passed + jmeter.passed;
        result.failed = k6.failed + jmeter.failed;
        result.durationSeconds = k6.durationSeconds + jmeter.durationSeconds;
        result.throughputPerSecond = result.durationSeconds == 0 ? 0.0
                : result.requests / (double) result.durationSeconds;
        result.errorRatePercent = result.requests == 0 ? 0.0
                : result.failed * 100.0 / result.requests;
        result.availabilityPercent = 100.0 - result.errorRatePercent;
        result.averageMs = weightedAverage(k6.averageMs, k6.requests, jmeter.averageMs, jmeter.requests);
        result.medianMs = Math.max(k6.medianMs, jmeter.medianMs);
        result.p90Ms = Math.max(k6.p90Ms, jmeter.p90Ms);
        result.p95Ms = Math.max(k6.p95Ms, jmeter.p95Ms);
        result.p99Ms = Math.max(k6.p99Ms, jmeter.p99Ms);
        result.minimumMs = minimumPositive(k6.minimumMs, jmeter.minimumMs);
        result.maximumMs = Math.max(k6.maximumMs, jmeter.maximumMs);
        result.bytesReceived = k6.bytesReceived + jmeter.bytesReceived;
        result.bytesSent = k6.bytesSent + jmeter.bytesSent;
        result.virtualUsers = k6.concurrency;
        result.jmeterThreads = jmeter.concurrency;

        applyDiagnostics(result);
        return result;
    }

    public Path write(EnterprisePerformanceMetrics metrics, Path output) throws IOException {
        Files.createDirectories(output.getParent());
        mapper.writerWithDefaultPrettyPrinter().writeValue(output.toFile(), metrics);
        return output;
    }

    private EnterprisePerformanceMetrics.EngineMetrics parseK6(Path file) throws IOException {
        EnterprisePerformanceMetrics.EngineMetrics metrics = new EnterprisePerformanceMetrics.EngineMetrics();
        metrics.engine = "k6";
        if (!Files.isRegularFile(file)) {
            return metrics;
        }

        JsonNode root = mapper.readTree(file.toFile());
        JsonNode all = root.path("metrics");
        JsonNode duration = all.path("http_req_duration");
        JsonNode requests = all.path("http_reqs");
        JsonNode failures = all.path("http_req_failed");
        JsonNode checks = all.path("checks");

        metrics.requests = intValue(requests, "count");
        metrics.failed = (int) Math.round(metrics.requests * doubleValue(failures, "value"));
        metrics.passed = Math.max(0, metrics.requests - metrics.failed);
        if (metrics.requests == 0) {
            metrics.passed = intValue(checks, "passes");
            metrics.failed = intValue(checks, "fails");
        }
        metrics.errorRatePercent = metrics.requests == 0 ? 0.0 : metrics.failed * 100.0 / metrics.requests;
        metrics.throughputPerSecond = doubleValue(requests, "rate");
        metrics.averageMs = doubleValue(duration, "avg");
        metrics.medianMs = doubleValue(duration, "med");
        metrics.p90Ms = doubleValue(duration, "p(90)");
        metrics.p95Ms = doubleValue(duration, "p(95)");
        metrics.p99Ms = duration.has("p(99)") ? doubleValue(duration, "p(99)") : metrics.maximumMs;
        metrics.minimumMs = doubleValue(duration, "min");
        metrics.maximumMs = doubleValue(duration, "max");
        if (metrics.p99Ms == 0.0) {
            metrics.p99Ms = metrics.maximumMs;
        }
        metrics.bytesReceived = longValue(all.path("data_received"), "count");
        metrics.bytesSent = longValue(all.path("data_sent"), "count");
        metrics.concurrency = intValue(all.path("vus_max"), "max");
        metrics.durationSeconds = metrics.throughputPerSecond <= 0.0 ? 0L
                : Math.max(1L, Math.round(metrics.requests / metrics.throughputPerSecond));
        return metrics;
    }

    private EnterprisePerformanceMetrics.EngineMetrics parseJMeter(Path file) throws IOException {
        EnterprisePerformanceMetrics.EngineMetrics metrics = new EnterprisePerformanceMetrics.EngineMetrics();
        metrics.engine = "JMeter";
        if (!Files.isRegularFile(file)) {
            return metrics;
        }

        List<Double> elapsedValues = new ArrayList<>();
        long minTimestamp = Long.MAX_VALUE;
        long maxEndTimestamp = 0L;
        long bytesReceived = 0L;
        long bytesSent = 0L;
        int maxThreads = 0;

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return metrics;
            }
            List<String> headers = parseCsvLine(headerLine);
            int timestampIndex = indexOf(headers, "timeStamp");
            int elapsedIndex = indexOf(headers, "elapsed");
            int successIndex = indexOf(headers, "success");
            int bytesIndex = indexOf(headers, "bytes");
            int sentBytesIndex = indexOf(headers, "sentBytes");
            int allThreadsIndex = indexOf(headers, "allThreads");

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                List<String> values = parseCsvLine(line);
                double elapsed = doubleAt(values, elapsedIndex);
                elapsedValues.add(elapsed);
                boolean success = successIndex >= 0 && successIndex < values.size()
                        && Boolean.parseBoolean(values.get(successIndex).trim());
                if (success) metrics.passed++; else metrics.failed++;
                long timestamp = longAt(values, timestampIndex);
                minTimestamp = timestamp > 0 ? Math.min(minTimestamp, timestamp) : minTimestamp;
                maxEndTimestamp = Math.max(maxEndTimestamp, timestamp + Math.round(elapsed));
                bytesReceived += longAt(values, bytesIndex);
                bytesSent += longAt(values, sentBytesIndex);
                maxThreads = Math.max(maxThreads, (int) longAt(values, allThreadsIndex));
            }
        }

        metrics.requests = elapsedValues.size();
        metrics.errorRatePercent = metrics.requests == 0 ? 0.0 : metrics.failed * 100.0 / metrics.requests;
        if (elapsedValues.isEmpty()) {
            return metrics;
        }
        Collections.sort(elapsedValues);
        metrics.minimumMs = elapsedValues.get(0);
        metrics.maximumMs = elapsedValues.get(elapsedValues.size() - 1);
        metrics.averageMs = elapsedValues.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        metrics.medianMs = percentile(elapsedValues, 50);
        metrics.p90Ms = percentile(elapsedValues, 90);
        metrics.p95Ms = percentile(elapsedValues, 95);
        metrics.p99Ms = percentile(elapsedValues, 99);
        metrics.durationSeconds = minTimestamp == Long.MAX_VALUE ? 0L
                : Math.max(1L, Math.round((maxEndTimestamp - minTimestamp) / 1000.0));
        metrics.throughputPerSecond = metrics.durationSeconds == 0 ? 0.0
                : metrics.requests / (double) metrics.durationSeconds;
        metrics.bytesReceived = bytesReceived;
        metrics.bytesSent = bytesSent;
        metrics.concurrency = maxThreads;
        return metrics;
    }

    private void applyDiagnostics(EnterprisePerformanceMetrics result) {
        boolean p95Pass = result.p95Ms < 500.0;
        boolean errorPass = result.errorRatePercent < 1.0;
        boolean availabilityPass = result.availabilityPercent >= 99.0;
        result.qualityGate = p95Pass && errorPass && availabilityPass ? "PASS" : "FAIL";

        if (!errorPass) {
            result.bottleneck = "Elevated request failures";
            result.recommendation = "Inspect server errors, dependency failures and retry behavior using correlation IDs.";
            result.confidencePercent = 96;
        } else if (!p95Pass) {
            result.bottleneck = "High tail latency";
            result.recommendation = "Profile the slowest endpoint, database calls and connection-pool saturation.";
            result.confidencePercent = 92;
        } else if (result.maximumMs > Math.max(1000.0, result.p95Ms * 3.0)) {
            result.bottleneck = "Latency outliers";
            result.recommendation = "Review isolated slow transactions and downstream dependency timing.";
            result.confidencePercent = 84;
        } else {
            result.bottleneck = "No critical bottleneck detected";
            result.recommendation = "Proceed to load, stress, spike and soak profiles while monitoring infrastructure telemetry.";
            result.confidencePercent = 90;
        }
    }

    private double weightedAverage(double a, int countA, double b, int countB) {
        int total = countA + countB;
        return total == 0 ? 0.0 : ((a * countA) + (b * countB)) / total;
    }

    private double minimumPositive(double a, double b) {
        if (a <= 0) return b;
        if (b <= 0) return a;
        return Math.min(a, b);
    }

    private int intValue(JsonNode node, String field) {
        return node.path(field).asInt(0);
    }

    private long longValue(JsonNode node, String field) {
        return node.path(field).asLong(0L);
    }

    private double doubleValue(JsonNode node, String field) {
        return node.path(field).asDouble(0.0);
    }

    private double percentile(List<Double> values, int percentile) {
        if (values.isEmpty()) return 0.0;
        int index = (int) Math.ceil(percentile / 100.0 * values.size()) - 1;
        return values.get(Math.max(0, Math.min(index, values.size() - 1)));
    }

    private int indexOf(List<String> headers, String expected) {
        for (int index = 0; index < headers.size(); index++) {
            if (expected.equalsIgnoreCase(headers.get(index).trim())) return index;
        }
        return -1;
    }

    private long longAt(List<String> values, int index) {
        if (index < 0 || index >= values.size()) return 0L;
        try { return Long.parseLong(values.get(index).trim()); }
        catch (NumberFormatException ignored) { return 0L; }
    }

    private double doubleAt(List<String> values, int index) {
        if (index < 0 || index >= values.size()) return 0.0;
        try { return Double.parseDouble(values.get(index).trim()); }
        catch (NumberFormatException ignored) { return 0.0; }
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (c == ',' && !quoted) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        values.add(current.toString());
        return values;
    }
}
