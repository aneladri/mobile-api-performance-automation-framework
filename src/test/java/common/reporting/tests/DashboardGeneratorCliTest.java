package common.reporting.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.reporting.dashboard.generator.DashboardGeneratorCli;
import common.reporting.model.ExecutionEnvironment;
import common.reporting.model.ExecutionMetrics;
import common.reporting.model.ExecutionStatus;
import common.reporting.model.ExecutionSummary;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DashboardGeneratorCliTest {

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    @Test
    public void shouldReadAvailableSummaryFiles()
            throws Exception {

        Path directory =
                Files.createTempDirectory(
                        "mapaf-dashboard-cli"
                );

        Path apiFile =
                directory.resolve("api-summary.json");

        Path performanceFile =
                directory.resolve(
                        "performance-summary.json"
                );

        writeSummary(
                apiFile,
                createSummary("API")
        );

        writeSummary(
                performanceFile,
                createSummary("Performance")
        );

        List<ExecutionSummary> summaries =
                DashboardGeneratorCli
                        .readAvailableSummaries(
                                apiFile,
                                performanceFile
                        );

        Assert.assertEquals(
                summaries.size(),
                2
        );

        Assert.assertEquals(
                summaries.get(0).getModule(),
                "API"
        );

        Assert.assertEquals(
                summaries.get(1).getModule(),
                "Performance"
        );
    }

    @Test
    public void shouldSkipMissingSummaryFiles()
            throws Exception {

        Path directory =
                Files.createTempDirectory(
                        "mapaf-dashboard-cli-missing"
                );

        Path apiFile =
                directory.resolve("api-summary.json");

        Path missingFile =
                directory.resolve("missing-summary.json");

        writeSummary(
                apiFile,
                createSummary("API")
        );

        List<ExecutionSummary> summaries =
                DashboardGeneratorCli
                        .readAvailableSummaries(
                                apiFile,
                                missingFile
                        );

        Assert.assertEquals(
                summaries.size(),
                1
        );

        Assert.assertEquals(
                summaries.get(0).getModule(),
                "API"
        );
    }

    @Test
    public void shouldReturnEmptyListForNullPaths()
            throws Exception {

        List<ExecutionSummary> summaries =
                DashboardGeneratorCli
                        .readAvailableSummaries(
                                (Path[]) null
                        );

        Assert.assertTrue(
                summaries.isEmpty()
        );
    }

    private void writeSummary(
            Path outputFile,
            ExecutionSummary summary
    ) throws Exception {

        objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValue(
                        outputFile.toFile(),
                        summary
                );
    }

    private ExecutionSummary createSummary(
            String module
    ) {
        return new ExecutionSummary(
                module,
                ExecutionStatus.PASS,
                ExecutionMetrics.fromCounts(
                        10,
                        10,
                        0,
                        0,
                        15
                ),
                new ExecutionEnvironment(
                        "QA",
                        "121",
                        "sprint-3-dashboard-cli",
                        "abc123",
                        module.toLowerCase() + "-001"
                )
        );
    }
}
