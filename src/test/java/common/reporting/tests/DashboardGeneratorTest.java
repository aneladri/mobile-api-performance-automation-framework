package common.reporting.tests;

import common.reporting.aggregation.DashboardAggregator;
import common.reporting.dashboard.DashboardSummary;
import common.reporting.dashboard.config.DashboardConfiguration;
import common.reporting.dashboard.generator.DashboardGenerator;
import common.reporting.model.ExecutionEnvironment;
import common.reporting.model.ExecutionMetrics;
import common.reporting.model.ExecutionStatus;
import common.reporting.model.ExecutionSummary;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DashboardGeneratorTest {

    @Test
    public void shouldGenerateDashboardUsingDefaultConfiguration()
            throws Exception {

        Path generatedFile =
                new DashboardGenerator().generate(
                        createDashboardSummary()
                );

        Assert.assertTrue(
                Files.exists(generatedFile)
        );

        Assert.assertEquals(
                generatedFile.getFileName().toString(),
                "index.html"
        );

        String html = Files.readString(
                generatedFile,
                StandardCharsets.UTF_8
        );

        Assert.assertTrue(
                html.contains(
                        "MAPAF Quality Engineering Dashboard"
                )
        );

        Assert.assertTrue(
                html.contains("Overview")
        );

        Assert.assertTrue(
                html.contains("API")
        );

        Assert.assertTrue(
                html.contains("Performance")
        );
    }

    @Test
    public void shouldGenerateDashboardInCustomDirectory()
            throws Exception {

        Path outputDirectory =
                Files.createTempDirectory(
                        "mapaf-dashboard-generator"
                );

        Path generatedFile =
                new DashboardGenerator().generate(
                        createDashboardSummary(),
                        DashboardConfiguration.defaultConfiguration(),
                        outputDirectory
                );

        Assert.assertTrue(
                Files.exists(generatedFile)
        );

        Assert.assertEquals(
                generatedFile.getParent(),
                outputDirectory.toAbsolutePath()
        );
    }

    @Test
    public void shouldUseConfiguredReportFileName()
            throws Exception {

        DashboardConfiguration configuration =
                DashboardConfiguration.defaultConfiguration();

        configuration.setReportFileName(
                "quality-dashboard.html"
        );

        Path outputDirectory =
                Files.createTempDirectory(
                        "mapaf-dashboard-filename"
                );

        Path generatedFile =
                new DashboardGenerator().generate(
                        createDashboardSummary(),
                        configuration,
                        outputDirectory
                );

        Assert.assertEquals(
                generatedFile.getFileName().toString(),
                "quality-dashboard.html"
        );

        Assert.assertTrue(
                Files.exists(generatedFile)
        );
    }

    @Test
    public void shouldUseCustomDashboardConfiguration()
            throws Exception {

        DashboardConfiguration configuration =
                DashboardConfiguration.defaultConfiguration();

        configuration.setTitle(
                "MAPAF Test Execution Report"
        );

        configuration.setProductName(
                "MAPAF Platform"
        );

        configuration.setVersion(
                "3.0"
        );

        Path outputDirectory =
                Files.createTempDirectory(
                        "mapaf-dashboard-configuration"
                );

        Path generatedFile =
                new DashboardGenerator().generate(
                        createDashboardSummary(),
                        configuration,
                        outputDirectory
                );

        String html = Files.readString(
                generatedFile,
                StandardCharsets.UTF_8
        );

        Assert.assertTrue(
                html.contains(
                        "MAPAF Test Execution Report"
                )
        );

        Assert.assertTrue(
                html.contains("MAPAF Platform")
        );

        Assert.assertTrue(
                html.contains("Version 3.0")
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Dashboard summary must not be null"
    )
    public void shouldRejectNullDashboardSummary()
            throws Exception {

        new DashboardGenerator().generate(null);
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Dashboard configuration must not be null"
    )
    public void shouldRejectNullDashboardConfiguration()
            throws Exception {

        new DashboardGenerator().generate(
                createDashboardSummary(),
                null,
                Files.createTempDirectory(
                        "mapaf-dashboard-null-config"
                )
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Dashboard output directory must not be null"
    )
    public void shouldRejectNullOutputDirectory()
            throws Exception {

        new DashboardGenerator().generate(
                createDashboardSummary(),
                DashboardConfiguration.defaultConfiguration(),
                null
        );
    }

    private DashboardSummary createDashboardSummary() {
        ExecutionSummary api = new ExecutionSummary(
                "API",
                ExecutionStatus.PASS,
                ExecutionMetrics.fromCounts(
                        9,
                        9,
                        0,
                        0,
                        5
                ),
                new ExecutionEnvironment(
                        "QA",
                        "120",
                        "sprint-3-dashboard-generator",
                        "abc123",
                        "api-001"
                )
        );

        ExecutionSummary performance = new ExecutionSummary(
                "Performance",
                ExecutionStatus.PASS,
                ExecutionMetrics.fromCounts(
                        160,
                        160,
                        0,
                        0,
                        20
                ),
                new ExecutionEnvironment(
                        "QA",
                        "120",
                        "sprint-3-dashboard-generator",
                        "abc123",
                        "performance-001"
                )
        );

        return new DashboardAggregator().aggregate(
                List.of(api, performance)
        );
    }
}
