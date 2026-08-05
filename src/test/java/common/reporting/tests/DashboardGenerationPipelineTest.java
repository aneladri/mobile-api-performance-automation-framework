package common.reporting.tests;

import common.reporting.dashboard.config.DashboardConfiguration;
import common.reporting.dashboard.config.DashboardGenerationConfiguration;
import common.reporting.dashboard.pipeline.DashboardGenerationPipeline;
import common.reporting.io.SummaryWriter;
import common.reporting.model.ExecutionEnvironment;
import common.reporting.model.ExecutionMetrics;
import common.reporting.model.ExecutionStatus;
import common.reporting.model.ExecutionSummary;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class DashboardGenerationPipelineTest {

        @Test
        public void shouldGenerateAndPublishDashboardEndToEnd()
                        throws Exception {

                Path workingDirectory = Files.createTempDirectory(
                                "mapaf-dashboard-e2e");

                Path summaryDirectory = workingDirectory.resolve("summaries");

                Path generationDirectory = workingDirectory.resolve("generated");

                Path publicationDirectory = workingDirectory.resolve("published");

                Files.createDirectories(summaryDirectory);

                Path apiSummaryFile = summaryDirectory.resolve(
                                "api-summary.json");

                Path performanceSummaryFile = summaryDirectory.resolve(
                                "performance-summary.json");

                SummaryWriter.write(
                                createSummary(
                                                "API",
                                                12,
                                                11,
                                                1,
                                                0),
                                apiSummaryFile);

                SummaryWriter.write(
                                createSummary(
                                                "Performance",
                                                100,
                                                100,
                                                0,
                                                0),
                                performanceSummaryFile);

                DashboardConfiguration dashboardConfiguration = DashboardConfiguration.defaultConfiguration();

                dashboardConfiguration.setTitle(
                                "MAPAF End-to-End Dashboard");

                dashboardConfiguration.setReportFileName(
                                "index.html");

                DashboardGenerationConfiguration configuration = DashboardGenerationConfiguration.builder()
                                .addSummaryPath(apiSummaryFile)
                                .addSummaryPath(performanceSummaryFile)
                                .outputDirectory(generationDirectory)
                                .dashboardConfiguration(
                                                dashboardConfiguration)
                                .build();

                Path publishedDashboard = new DashboardGenerationPipeline().execute(
                                configuration,
                                publicationDirectory);

                Assert.assertTrue(
                                Files.exists(publishedDashboard));

                Assert.assertEquals(
                                publishedDashboard.getParent(),
                                publicationDirectory.toAbsolutePath());

                Assert.assertEquals(
                                publishedDashboard.getFileName().toString(),
                                "index.html");

                String html = Files.readString(
                                publishedDashboard,
                                StandardCharsets.UTF_8);

                Assert.assertTrue(
                                html.contains(
                                                "MAPAF End-to-End Dashboard"));

                Assert.assertTrue(
                                html.contains("Overview"));

                Assert.assertTrue(
                                html.contains("API"));

                Assert.assertTrue(
                                html.contains("Performance"));

                Assert.assertTrue(
                                html.contains("Environment"));

                Assert.assertTrue(
                                html.contains("Downloads"));

                Assert.assertTrue(
                                html.contains("FAIL"));

                Assert.assertTrue(
                                Files.exists(
                                                generationDirectory.resolve(
                                                                "index.html")));
        }

        @Test
        public void shouldGenerateDashboardWhenOneSummaryIsMissing()
                        throws Exception {

                Path workingDirectory = Files.createTempDirectory(
                                "mapaf-dashboard-partial-e2e");

                Path apiSummaryFile = workingDirectory.resolve(
                                "api-summary.json");

                Path missingPerformanceFile = workingDirectory.resolve(
                                "missing-performance-summary.json");

                SummaryWriter.write(
                                createSummary(
                                                "API",
                                                10,
                                                10,
                                                0,
                                                0),
                                apiSummaryFile);

                DashboardGenerationConfiguration configuration = DashboardGenerationConfiguration.builder()
                                .addSummaryPath(apiSummaryFile)
                                .addSummaryPath(
                                                missingPerformanceFile)
                                .outputDirectory(
                                                workingDirectory.resolve(
                                                                "generated"))
                                .build();

                Path publishedDashboard = new DashboardGenerationPipeline().execute(
                                configuration,
                                workingDirectory.resolve(
                                                "published"));

                Assert.assertTrue(
                                Files.exists(publishedDashboard));

                String html = Files.readString(
                                publishedDashboard,
                                StandardCharsets.UTF_8);

                Assert.assertTrue(
                                html.contains("API"));

                Assert.assertTrue(
                                html.contains("Performance"));

                Assert.assertTrue(
                                html.contains("Data not available"));
        }

        @Test
        public void shouldGenerateDashboardWhenAllSummariesAreMissing()
                        throws Exception {

                Path workingDirectory = Files.createTempDirectory(
                                "mapaf-dashboard-empty-e2e");

                DashboardGenerationConfiguration configuration = DashboardGenerationConfiguration.builder()
                                .addSummaryPath(
                                                workingDirectory.resolve(
                                                                "missing-api.json"))
                                .addSummaryPath(
                                                workingDirectory.resolve(
                                                                "missing-performance.json"))
                                .outputDirectory(
                                                workingDirectory.resolve(
                                                                "generated"))
                                .build();

                Path publishedDashboard = new DashboardGenerationPipeline().execute(
                                configuration,
                                workingDirectory.resolve(
                                                "published"));

                Assert.assertTrue(
                                Files.exists(publishedDashboard));

                String html = Files.readString(
                                publishedDashboard,
                                StandardCharsets.UTF_8);

                Assert.assertTrue(
                                html.contains("NOT_RUN"));

                Assert.assertTrue(
                                html.contains("Data not available"));
        }

        @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Dashboard generation configuration must not be null")
        public void shouldRejectNullGenerationConfiguration()
                        throws Exception {

                new DashboardGenerationPipeline().execute(
                                null,
                                Files.createTempDirectory(
                                                "mapaf-null-generation-config"));
        }

        @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Dashboard publication directory must not be null")
        public void shouldRejectNullPublicationDirectory()
                        throws Exception {

                DashboardGenerationConfiguration configuration = DashboardGenerationConfiguration
                                .defaultConfiguration();

                new DashboardGenerationPipeline().execute(
                                configuration,
                                null);
        }

        private ExecutionSummary createSummary(
                        String module,
                        int total,
                        int passed,
                        int failed,
                        int skipped) {
                ExecutionStatus status;

                if (failed > 0) {
                        status = ExecutionStatus.FAIL;
                } else if (skipped > 0) {
                        status = ExecutionStatus.PARTIAL;
                } else {
                        status = ExecutionStatus.PASS;
                }

                return new ExecutionSummary(
                                module,
                                status,
                                ExecutionMetrics.fromCounts(
                                                total,
                                                passed,
                                                failed,
                                                skipped,
                                                20),
                                new ExecutionEnvironment(
                                                "QA",
                                                "130",
                                                "sprint-3-dashboard-publisher",
                                                "abc123",
                                                module.toLowerCase() + "-e2e"));
        }
}
