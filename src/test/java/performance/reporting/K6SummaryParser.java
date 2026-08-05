package performance.reporting;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class K6SummaryParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public PerformanceParseResult parseDirectory(Path directory)
            throws IOException {

        PerformanceParseResult result = new PerformanceParseResult();

        if (directory == null || !Files.isDirectory(directory)) {
            return result;
        }

        try (Stream<Path> files = Files.walk(directory)) {
            List<Path> jsonFiles = files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName()
                            .toString()
                            .toLowerCase(Locale.ROOT)
                            .endsWith(".json"))
                    .toList();

            for (Path file : jsonFiles) {
                parseFile(file, result);
            }
        }

        return result;
    }

    private void parseFile(
            Path file,
            PerformanceParseResult result
    ) throws IOException {

        JsonNode root = objectMapper.readTree(file.toFile());
        JsonNode metrics = root.path("metrics");

        JsonNode checks = metrics.path("checks");
        JsonNode requests = metrics.path("http_reqs");
        JsonNode requestFailures = metrics.path("http_req_failed");

        long passed = firstLong(
                checks.path("passes"),
                checks.path("values").path("passes")
        );

        long failed = firstLong(
                checks.path("fails"),
                checks.path("values").path("fails")
        );

        long requestCount = firstLong(
                requests.path("count"),
                requests.path("values").path("count")
        );

        double failureRate = firstDouble(
                requestFailures.path("value"),
                requestFailures.path("rate"),
                requestFailures.path("values").path("rate")
        );

        if (passed == 0 && failed == 0 && requestCount > 0) {
            failed = Math.round(requestCount * failureRate);
            passed = Math.max(0, requestCount - failed);
        }

        long durationMilliseconds = firstLong(
                root.path("state").path("testRunDurationMs"),
                root.path("testRunDurationMs")
        );

        result.add(
                Math.toIntExact(passed + failed),
                Math.toIntExact(passed),
                Math.toIntExact(failed),
                millisecondsToSeconds(durationMilliseconds),
                Math.toIntExact(requestCount)
        );
    }

    private long firstLong(JsonNode... nodes) {
        for (JsonNode node : nodes) {
            if (node != null && node.isNumber()) {
                return node.asLong();
            }
        }

        return 0;
    }

    private double firstDouble(JsonNode... nodes) {
        for (JsonNode node : nodes) {
            if (node != null && node.isNumber()) {
                return node.asDouble();
            }
        }

        return 0.0;
    }

    private long millisecondsToSeconds(long milliseconds) {
        if (milliseconds <= 0) {
            return 0;
        }

        return Math.max(1, Math.round(milliseconds / 1000.0));
    }
}
