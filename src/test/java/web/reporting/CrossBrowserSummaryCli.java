package web.reporting;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class CrossBrowserSummaryCli {

    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper()
                    .enable(SerializationFeature.INDENT_OUTPUT);

    private static final List<Path> BROWSER_SUMMARIES = List.of(
            Path.of("web/reports/chromium-summary.json"),
            Path.of("web/reports/firefox-summary.json"),
            Path.of("web/reports/webkit-summary.json")
    );

    private static final Path DEFAULT_OUTPUT =
            Path.of("web/reports/cross-browser-summary.json");

    private CrossBrowserSummaryCli() {
    }

    public static void main(String[] args) throws IOException {
        Path outputFile =
                args.length > 0
                        ? Path.of(args[0])
                        : DEFAULT_OUTPUT;

        ObjectNode aggregateSummary =
                createAggregateSummary(BROWSER_SUMMARIES);

        Path parentDirectory = outputFile.getParent();

        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        OBJECT_MAPPER.writeValue(
                outputFile.toFile(),
                aggregateSummary
        );

        System.out.println(
                "Cross-browser summary generated successfully: "
                        + outputFile.toAbsolutePath()
        );
    }

    static ObjectNode createAggregateSummary(
            List<Path> summaryFiles
    ) throws IOException {

        ArrayNode browserResults =
                OBJECT_MAPPER.createArrayNode();

        int total = 0;
        int passed = 0;
        int failed = 0;
        int skipped = 0;
        long durationSeconds = 0;

        int availableBrowsers = 0;
        int missingBrowsers = 0;

        String overallStatus = "PASS";

        for (Path summaryFile : summaryFiles) {
            if (!Files.exists(summaryFile)) {
                browserResults.add(
                        createMissingBrowserResult(summaryFile)
                );

                missingBrowsers++;
                overallStatus = "INCOMPLETE";
                continue;
            }

            JsonNode summary =
                    OBJECT_MAPPER.readTree(summaryFile.toFile());

            JsonNode metrics = summary.path("metrics");
            JsonNode details = summary.path("details");

            String browser =
                    details.path("browser").asText("UNKNOWN");

            String browserStatus =
                    summary.path("status").asText("UNKNOWN");

            int browserTotal =
                    metrics.path("total").asInt();

            int browserPassed =
                    metrics.path("passed").asInt();

            int browserFailed =
                    metrics.path("failed").asInt();

            int browserSkipped =
                    metrics.path("skipped").asInt();

            long browserDuration =
                    metrics.path("durationSeconds").asLong();

            total += browserTotal;
            passed += browserPassed;
            failed += browserFailed;
            skipped += browserSkipped;
            durationSeconds += browserDuration;
            availableBrowsers++;

            if ("FAIL".equalsIgnoreCase(browserStatus)
                    || browserFailed > 0) {

                overallStatus = "FAIL";
            }

            browserResults.add(
                    createBrowserResult(
                            browser,
                            browserStatus,
                            summaryFile,
                            browserTotal,
                            browserPassed,
                            browserFailed,
                            browserSkipped,
                            browserDuration,
                            metrics.path("passRate").asDouble()
                    )
            );
        }

        double passRate =
                total == 0
                        ? 0.0
                        : ((double) passed / total) * 100.0;

        ObjectNode root = OBJECT_MAPPER.createObjectNode();

        root.put("contractVersion", "1.0");
        root.put("module", "Web Cross Browser");
        root.put("status", overallStatus);

        ObjectNode metrics = root.putObject("metrics");

        metrics.put("total", total);
        metrics.put("passed", passed);
        metrics.put("failed", failed);
        metrics.put("skipped", skipped);
        metrics.put("passRate", round(passRate));
        metrics.put("durationSeconds", durationSeconds);
        metrics.put("availableBrowsers", availableBrowsers);
        metrics.put("missingBrowsers", missingBrowsers);

        ObjectNode environment = root.putObject("environment");

        environment.put(
                "environment",
                System.getProperty(
                        "mapaf.environment",
                        "LOCAL"
                )
        );

        environment.put(
                "build",
                System.getProperty(
                        "mapaf.build",
                        "local"
                )
        );

        environment.put(
                "branch",
                System.getProperty(
                        "mapaf.branch",
                        "local"
                )
        );

        environment.put(
                "commit",
                System.getProperty(
                        "mapaf.commit",
                        "local"
                )
        );

        environment.put(
                "executionId",
                createExecutionId()
        );

        environment.put(
                "generatedAt",
                Instant.now().toString()
        );

        root.set("browsers", browserResults);

        ObjectNode links = root.putObject("links");

        links.put(
                "chromiumSummary",
                "web/reports/chromium-summary.json"
        );

        links.put(
                "firefoxSummary",
                "web/reports/firefox-summary.json"
        );

        links.put(
                "webkitSummary",
                "web/reports/webkit-summary.json"
        );

        return root;
    }

    private static ObjectNode createBrowserResult(
            String browser,
            String status,
            Path source,
            int total,
            int passed,
            int failed,
            int skipped,
            long durationSeconds,
            double passRate
    ) {

        ObjectNode result =
                OBJECT_MAPPER.createObjectNode();

        result.put("browser", browser);
        result.put("status", status);

        ObjectNode metrics = result.putObject("metrics");

        metrics.put("total", total);
        metrics.put("passed", passed);
        metrics.put("failed", failed);
        metrics.put("skipped", skipped);
        metrics.put("passRate", passRate);
        metrics.put("durationSeconds", durationSeconds);

        result.put(
                "source",
                source.toString()
        );

        return result;
    }

    private static ObjectNode createMissingBrowserResult(
            Path summaryFile
    ) {

        ObjectNode result =
                OBJECT_MAPPER.createObjectNode();

        result.put(
                "browser",
                inferBrowser(summaryFile)
        );

        result.put("status", "MISSING");
        result.put("source", summaryFile.toString());

        return result;
    }

    private static String inferBrowser(Path path) {
        String filename =
                path.getFileName()
                        .toString()
                        .toUpperCase();

        if (filename.contains("CHROMIUM")) {
            return "CHROMIUM";
        }

        if (filename.contains("FIREFOX")) {
            return "FIREFOX";
        }

        if (filename.contains("WEBKIT")) {
            return "WEBKIT";
        }

        return "UNKNOWN";
    }

    private static String createExecutionId() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                                "yyyyMMdd-HHmmss"
                        )
                        .withZone(ZoneOffset.UTC);

        return "web-cross-browser-"
                + formatter.format(Instant.now());
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
