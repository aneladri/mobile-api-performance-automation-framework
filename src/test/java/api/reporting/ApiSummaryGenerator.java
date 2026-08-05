package api.reporting;

import common.reporting.io.SummaryWriter;
import common.reporting.model.ExecutionEnvironment;
import common.reporting.model.ExecutionMetrics;
import common.reporting.model.ExecutionStatus;
import common.reporting.model.ExecutionSummary;

import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class ApiSummaryGenerator {

    private ApiSummaryGenerator() {
    }

    public static ExecutionSummary generate(
            Path testResultsDirectory
    ) throws Exception {

        ApiTestResultParser result =
                new GradleTestXmlParser()
                        .parseDirectory(testResultsDirectory);

        ExecutionStatus status = determineStatus(result);

        ExecutionMetrics metrics = ExecutionMetrics.fromCounts(
                result.getTotal(),
                result.getPassed(),
                result.getFailed(),
                result.getSkipped(),
                result.getDurationSeconds()
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
                "API",
                status,
                metrics,
                environment
        );

        summary.addDetail(
                "resultFiles",
                result.getFileCount()
        );

        summary.addDetail(
                "framework",
                "Gradle TestNG"
        );

        summary.addLink(
                "htmlReport",
                "build/reports/tests/apiTest/index.html"
        );

        summary.addLink(
                "xmlResults",
                "build/test-results/apiTest/"
        );

        return summary;
    }

    public static Path generateAndWrite(
            Path testResultsDirectory,
            Path outputFile
    ) throws Exception {

        return SummaryWriter.write(
                generate(testResultsDirectory),
                outputFile
        );
    }

    private static ExecutionStatus determineStatus(
            ApiTestResultParser result
    ) {
        if (result.getTotal() == 0) {
            return ExecutionStatus.NOT_RUN;
        }

        if (result.getFailed() > 0) {
            return ExecutionStatus.FAIL;
        }

        if (result.getSkipped() > 0) {
            return ExecutionStatus.PARTIAL;
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
        return "api-"
                + DateTimeFormatter
                .ofPattern("yyyyMMdd-HHmmss")
                .withZone(ZoneOffset.UTC)
                .format(Instant.now());
    }
}
