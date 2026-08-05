package performance.reporting;

import common.reporting.io.SummaryWriter;
import common.reporting.model.ExecutionEnvironment;
import common.reporting.model.ExecutionMetrics;
import common.reporting.model.ExecutionStatus;
import common.reporting.model.ExecutionSummary;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class PerformanceSummaryGenerator {

    private PerformanceSummaryGenerator() {
    }

    public static ExecutionSummary generate(
            Path k6Directory,
            Path jmeterDirectory
    ) throws IOException {

        PerformanceParseResult k6Result =
                new K6SummaryParser().parseDirectory(k6Directory);

        PerformanceParseResult jmeterResult =
                new JMeterSummaryParser().parseDirectory(
                        jmeterDirectory
                );

        PerformanceParseResult combined =
                new PerformanceParseResult();

        combined.merge(k6Result);
        combined.merge(jmeterResult);

        ExecutionStatus status = determineStatus(combined);

        ExecutionMetrics metrics = ExecutionMetrics.fromCounts(
                combined.getTotal(),
                combined.getPassed(),
                combined.getFailed(),
                0,
                combined.getDurationSeconds()
        );

        ExecutionEnvironment environment =
                new ExecutionEnvironment(
                        readSetting(
                                "MAPAF_ENV",
                                "mapaf.environment",
                                "LOCAL"
                        ),
                        readSetting(
                                "BUILD_NUMBER",
                                "mapaf.build",
                                "local"
                        ),
                        readSetting(
                                "GIT_BRANCH",
                                "mapaf.branch",
                                "local"
                        ),
                        readSetting(
                                "GIT_COMMIT",
                                "mapaf.commit",
                                "local"
                        ),
                        createExecutionId()
                );

        ExecutionSummary summary = new ExecutionSummary(
                "Performance",
                status,
                metrics,
                environment
        );

        summary.addDetail("k6Files", k6Result.getFileCount());
        summary.addDetail(
                "jmeterFiles",
                jmeterResult.getFileCount()
        );
        summary.addDetail(
                "k6Validations",
                k6Result.getTotal()
        );
        summary.addDetail(
                "jmeterSamples",
                jmeterResult.getTotal()
        );
        summary.addDetail(
                "requestCount",
                combined.getRequestCount()
        );

        summary.addLink(
                "dashboard",
                "performance/reports/index.html"
        );
        summary.addLink(
                "k6Reports",
                "performance/k6/reports/"
        );
        summary.addLink(
                "jmeterReports",
                "performance/jmeter/reports/"
        );

        return summary;
    }

    public static Path generateAndWrite(
            Path k6Directory,
            Path jmeterDirectory,
            Path outputFile
    ) throws IOException {

        return SummaryWriter.write(
                generate(k6Directory, jmeterDirectory),
                outputFile
        );
    }

    private static ExecutionStatus determineStatus(
            PerformanceParseResult result
    ) {
        if (result.getTotal() == 0) {
            return ExecutionStatus.NOT_RUN;
        }

        if (result.getFailed() > 0) {
            return ExecutionStatus.FAIL;
        }

        return ExecutionStatus.PASS;
    }

    private static String readSetting(
            String environmentVariable,
            String systemProperty,
            String defaultValue
    ) {
        String propertyValue = System.getProperty(systemProperty);

        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }

        String environmentValue = System.getenv(
                environmentVariable
        );

        if (environmentValue != null
                && !environmentValue.isBlank()) {

            return environmentValue;
        }

        return defaultValue;
    }

    private static String createExecutionId() {
        return "performance-"
                + DateTimeFormatter
                .ofPattern("yyyyMMdd-HHmmss")
                .withZone(ZoneOffset.UTC)
                .format(Instant.now());
    }
}
