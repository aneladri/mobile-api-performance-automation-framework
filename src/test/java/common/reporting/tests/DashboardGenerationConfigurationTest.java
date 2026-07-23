package common.reporting.tests;

import common.reporting.dashboard.config.DashboardConfiguration;
import common.reporting.dashboard.config.DashboardGenerationConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class DashboardGenerationConfigurationTest {

    @Test
    public void shouldCreateDefaultGenerationConfiguration() {

        DashboardGenerationConfiguration configuration =
                DashboardGenerationConfiguration
                        .defaultConfiguration();

        Assert.assertEquals(
                configuration.getSummaryPaths().size(),
                2
        );

        Assert.assertEquals(
                configuration.getSummaryPaths().get(0),
                Paths.get(
                        "api",
                        "reports",
                        "summary.json"
                )
        );

        Assert.assertEquals(
                configuration.getSummaryPaths().get(1),
                Paths.get(
                        "performance",
                        "reports",
                        "summary.json"
                )
        );

        Assert.assertEquals(
                configuration.getOutputDirectory(),
                Paths.get(
                        "dashboard",
                        "reports"
                )
        );

        Assert.assertNotNull(
                configuration.getDashboardConfiguration()
        );
    }

    @Test
    public void shouldCreateCustomGenerationConfiguration() {

        DashboardConfiguration dashboardConfiguration =
                DashboardConfiguration.defaultConfiguration();

        dashboardConfiguration.setTitle(
                "Custom Dashboard"
        );

        DashboardGenerationConfiguration configuration =
                DashboardGenerationConfiguration
                        .builder()
                        .addSummaryPath(
                                Paths.get(
                                        "custom",
                                        "api-summary.json"
                                )
                        )
                        .addSummaryPath(
                                Paths.get(
                                        "custom",
                                        "performance-summary.json"
                                )
                        )
                        .outputDirectory(
                                Paths.get(
                                        "custom",
                                        "dashboard"
                                )
                        )
                        .dashboardConfiguration(
                                dashboardConfiguration
                        )
                        .build();

        Assert.assertEquals(
                configuration.getSummaryPaths().size(),
                2
        );

        Assert.assertEquals(
                configuration.getOutputDirectory(),
                Paths.get(
                        "custom",
                        "dashboard"
                )
        );

        Assert.assertEquals(
                configuration
                        .getDashboardConfiguration()
                        .getTitle(),
                "Custom Dashboard"
        );
    }

    @Test
    public void shouldReplaceSummaryPaths() {

        List<Path> paths = List.of(
                Paths.get("ui-summary.json"),
                Paths.get("mobile-summary.json")
        );

        DashboardGenerationConfiguration configuration =
                DashboardGenerationConfiguration
                        .builder()
                        .summaryPaths(paths)
                        .build();

        Assert.assertEquals(
                configuration.getSummaryPaths(),
                paths
        );
    }

    @Test
    public void shouldProtectSummaryPathCollection() {

        DashboardGenerationConfiguration configuration =
                DashboardGenerationConfiguration
                        .builder()
                        .addSummaryPath(
                                Paths.get("api-summary.json")
                        )
                        .build();

        List<Path> returned =
                configuration.getSummaryPaths();

        returned.add(
                Paths.get("another-summary.json")
        );

        Assert.assertEquals(
                configuration.getSummaryPaths().size(),
                1
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Summary path must not be null"
    )
    public void shouldRejectNullSummaryPath() {

        DashboardGenerationConfiguration
                .builder()
                .addSummaryPath(null);
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Summary paths must not be null"
    )
    public void shouldRejectNullSummaryPathCollection() {

        DashboardGenerationConfiguration
                .builder()
                .summaryPaths(null);
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Summary paths must not contain null"
    )
    public void shouldRejectSummaryCollectionContainingNull() {

        DashboardGenerationConfiguration
                .builder()
                .summaryPaths(
                        Arrays.asList(
                                Paths.get("api-summary.json"),
                                null
                        )
                );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Dashboard output directory must not be null"
    )
    public void shouldRejectNullOutputDirectory() {

        DashboardGenerationConfiguration
                .builder()
                .outputDirectory(null);
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Dashboard configuration must not be null"
    )
    public void shouldRejectNullDashboardConfiguration() {

        DashboardGenerationConfiguration
                .builder()
                .dashboardConfiguration(null);
    }
}
