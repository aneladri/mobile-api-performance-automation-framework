package common.reporting.tests;

import common.reporting.aggregation.DashboardAggregator;
import common.reporting.dashboard.DashboardSummary;
import common.reporting.dashboard.builder.DashboardBuilder;
import common.reporting.dashboard.builder.DashboardPage;
import common.reporting.dashboard.config.DashboardConfiguration;
import common.reporting.dashboard.config.DashboardTab;
import common.reporting.dashboard.widget.DashboardSection;
import common.reporting.dashboard.widget.WidgetStatus;
import common.reporting.model.ExecutionEnvironment;
import common.reporting.model.ExecutionMetrics;
import common.reporting.model.ExecutionStatus;
import common.reporting.model.ExecutionSummary;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class DashboardBuilderTest {

    @Test
    public void shouldBuildConfiguredDashboardSections() {
        DashboardSummary summary = createDashboardSummary();

        DashboardPage page = new DashboardBuilder().build(
                summary,
                DashboardConfiguration.defaultConfiguration()
        );

        Assert.assertEquals(
                page.getVisibleSections().size(),
                5
        );

        Assert.assertEquals(
                page.getVisibleSections().get(0).getId(),
                "overview"
        );

        Assert.assertEquals(
                page.getVisibleSections().get(1).getId(),
                "api"
        );

        Assert.assertEquals(
                page.getVisibleSections().get(2).getId(),
                "performance"
        );
    }

    @Test
    public void shouldRespectEnabledDashboardTabs() {
        DashboardConfiguration configuration =
                DashboardConfiguration.defaultConfiguration();

        configuration.setEnabledTabs(
                List.of(
                        DashboardTab.OVERVIEW,
                        DashboardTab.API
                )
        );

        DashboardPage page = new DashboardBuilder().build(
                createDashboardSummary(),
                configuration
        );

        Assert.assertEquals(
                page.getVisibleSections().size(),
                2
        );

        Assert.assertEquals(
                page.getVisibleSections().get(1).getId(),
                "api"
        );
    }

    @Test
    public void shouldCreateNotAvailableWidgetForMissingModule() {
        ExecutionSummary api = createSummary(
                "API",
                ExecutionStatus.PASS,
                5,
                5,
                0,
                0
        );

        DashboardSummary summary =
                new DashboardAggregator().aggregate(
                        List.of(api)
                );

        DashboardPage page = new DashboardBuilder().build(
                summary,
                DashboardConfiguration.defaultConfiguration()
        );

        DashboardSection performanceSection =
                page.getVisibleSections().stream()
                        .filter(section ->
                                "performance".equals(
                                        section.getId()
                                )
                        )
                        .findFirst()
                        .orElseThrow();

        Assert.assertEquals(
                performanceSection
                        .getVisibleWidgets()
                        .get(0)
                        .getStatus(),
                WidgetStatus.NOT_AVAILABLE
        );
    }

    @Test
    public void shouldMapFailedExecutionToFailureWidget() {
        ExecutionSummary api = createSummary(
                "API",
                ExecutionStatus.FAIL,
                5,
                4,
                1,
                0
        );

        DashboardSummary summary =
                new DashboardAggregator().aggregate(
                        List.of(api)
                );

        DashboardPage page = new DashboardBuilder().build(
                summary,
                DashboardConfiguration.defaultConfiguration()
        );

        DashboardSection apiSection =
                page.getVisibleSections().stream()
                        .filter(section ->
                                "api".equals(section.getId())
                        )
                        .findFirst()
                        .orElseThrow();

        Assert.assertEquals(
                apiSection.getVisibleWidgets()
                        .get(0)
                        .getStatus(),
                WidgetStatus.FAILURE
        );
    }

    private DashboardSummary createDashboardSummary() {
        ExecutionSummary api = createSummary(
                "API",
                ExecutionStatus.PASS,
                9,
                9,
                0,
                0
        );

        ExecutionSummary performance = createSummary(
                "Performance",
                ExecutionStatus.PASS,
                160,
                160,
                0,
                0
        );

        api.addLink(
                "htmlReport",
                "build/reports/tests/apiTest/index.html"
        );

        performance.addLink(
                "dashboard",
                "performance/reports/index.html"
        );

        return new DashboardAggregator().aggregate(
                List.of(api, performance)
        );
    }

    private ExecutionSummary createSummary(
            String module,
            ExecutionStatus status,
            int total,
            int passed,
            int failed,
            int skipped
    ) {
        return new ExecutionSummary(
                module,
                status,
                ExecutionMetrics.fromCounts(
                        total,
                        passed,
                        failed,
                        skipped,
                        5
                ),
                new ExecutionEnvironment(
                        "QA",
                        "100",
                        "sprint-2-unified-dashboard",
                        "local",
                        module.toLowerCase() + "-001"
                )
        );
    }
}
