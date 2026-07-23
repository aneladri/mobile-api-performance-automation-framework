package web.reporting;

import common.reporting.io.SummaryWriter;
import common.reporting.model.ExecutionEnvironment;
import common.reporting.model.ExecutionMetrics;
import common.reporting.model.ExecutionStatus;
import common.reporting.model.ExecutionSummary;

import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class WebSummaryGenerator {

        private WebSummaryGenerator() {
        }

        public static ExecutionSummary generate(
                        Path testResultsDirectory) throws Exception {

                WebTestResultParser result = new GradleWebTestXmlParser()
                                .parseDirectory(
                                                testResultsDirectory);

                ExecutionStatus status = determineStatus(result);

                ExecutionMetrics metrics = ExecutionMetrics.fromCounts(
                                result.getTotal(),
                                result.getPassed(),
                                result.getFailed(),
                                result.getSkipped(),
                                Math.round(result.getDurationSeconds()));

                ExecutionEnvironment environment = new ExecutionEnvironment(
                                readSetting(
                                                "MAPAF_ENV",
                                                "mapaf.environment",
                                                "LOCAL"),
                                readSetting(
                                                "BUILD_NUMBER",
                                                "mapaf.build",
                                                "local"),
                                readSetting(
                                                "GIT_BRANCH",
                                                "mapaf.branch",
                                                "local"),
                                readSetting(
                                                "GIT_COMMIT",
                                                "mapaf.commit",
                                                "local"),
                                createExecutionId());

                ExecutionSummary summary = new ExecutionSummary(
                                "Web",
                                status,
                                metrics,
                                environment);

                summary.addDetail(
                                "resultFiles",
                                result.getFileCount());

                summary.addDetail(
                                "framework",
                                "Playwright Java TestNG");

                summary.addDetail(
                                "browser",
                                readSetting(
                                                "WEB_BROWSER",
                                                "web.browser",
                                                "CHROMIUM"));

                summary.addLink(
                                "htmlReport",
                                "build/reports/tests/webTest/index.html");

                summary.addLink(
                                "xmlResults",
                                "build/test-results/webTest/");

                summary.addLink(
                                "artifacts",
                                "web/artifacts/");

                return summary;
        }

        public static Path generateAndWrite(
                        Path testResultsDirectory,
                        Path outputFile) throws Exception {

                return SummaryWriter.write(
                                generate(testResultsDirectory),
                                outputFile);
        }

        private static ExecutionStatus determineStatus(
                        WebTestResultParser result) {
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
                        String defaultValue) {
                String propertyValue = System.getProperty(systemProperty);

                if (propertyValue != null
                                && !propertyValue.isBlank()) {

                        return propertyValue;
                }

                String environmentValue = System.getenv(environmentVariable);

                if (environmentValue != null
                                && !environmentValue.isBlank()) {

                        return environmentValue;
                }

                return defaultValue;
        }

        private static String createExecutionId() {
                return "web-"
                                + DateTimeFormatter
                                                .ofPattern("yyyyMMdd-HHmmss")
                                                .withZone(ZoneOffset.UTC)
                                                .format(Instant.now());
        }
}
