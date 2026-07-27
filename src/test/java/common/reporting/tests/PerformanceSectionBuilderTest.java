package common.reporting.tests;

import common.reporting.aggregation.DashboardAggregator;
import common.reporting.dashboard.DashboardSummary;
import common.reporting.dashboard.section.PerformanceSectionBuilder;
import common.reporting.dashboard.widget.DashboardSection;
import common.reporting.dashboard.widget.DashboardWidget;
import common.reporting.dashboard.widget.WidgetStatus;
import common.reporting.dashboard.widget.WidgetType;
import common.reporting.model.ExecutionEnvironment;
import common.reporting.model.ExecutionMetrics;
import common.reporting.model.ExecutionStatus;
import common.reporting.model.ExecutionSummary;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class PerformanceSectionBuilderTest {

    @Test
    public void shouldBuildPerformanceSection() {
        DashboardSection section =
                new PerformanceSectionBuilder().build(
                        createDashboardSummary(
                                ExecutionStatus.PASS,
                                160,
                                160,
                                0,
                                0
                        )
                );

        Assert.assertEquals(
                section.getId(),
                "performance"
        );

        Assert.assertEquals(
                section.getTitle(),
                "Performance"
        );

        Assert.assertEquals(
                section.getDisplayOrder(),
                3
        );

        Assert.assertEquals(
                section.getVisibleWidgets().size(),
                4
        );
    }

    @Test
    public void shouldPopulatePerformanceWidgets() {
        DashboardSection section =
                new PerformanceSectionBuilder().build(
                        createDashboardSummary(
                                ExecutionStatus.PASS,
                                160,
                                160,
                                0,
                                0
                        )
                );

        DashboardWidget status =
                findWidget(section, "performance-status");

        DashboardWidget module =
                findWidget(section, "performance-module");

        DashboardWidget passRate =
                findWidget(section, "performance-pass-rate");

        DashboardWidget duration =
                findWidget(section, "performance-duration");

        Assert.assertEquals(
                status.getStatus(),
                WidgetStatus.SUCCESS
        );

        Assert.assertEquals(
                status.getData().get("value"),
                "PASS"
        );

        Assert.assertEquals(
                module.getType(),
                WidgetType.MODULE
        );

        Assert.assertEquals(
                module.getData().get("total"),
                160
        );

        Assert.assertEquals(
                module.getData().get("passed"),
                160
        );

        Assert.assertEquals(
                module.getData().get("failed"),
                0
        );

        Assert.assertEquals(
                passRate.getData().get("unit"),
                "%"
        );

        Assert.assertEquals(
                duration.getData().get("unit"),
                "seconds"
        );
    }

    @Test
    public void shouldMapFailedPerformanceExecution() {
        DashboardSection section =
                new PerformanceSectionBuilder().build(
                        createDashboardSummary(
                                ExecutionStatus.FAIL,
                                100,
                                90,
                                10,
                                0
                        )
                );

        Assert.assertEquals(
                findWidget(
                        section,
                        "performance-status"
                ).getStatus(),
                WidgetStatus.FAILURE
        );

        Assert.assertEquals(
                findWidget(
                        section,
                        "performance-module"
                ).getStatus(),
                WidgetStatus.FAILURE
        );
    }

    @Test
    public void shouldMapPartialPerformanceExecution() {
        DashboardSection section =
                new PerformanceSectionBuilder().build(
                        createDashboardSummary(
                                ExecutionStatus.PARTIAL,
                                100,
                                95,
                                3,
                                2
                        )
                );

        Assert.assertEquals(
                findWidget(
                        section,
                        "performance-status"
                ).getStatus(),
                WidgetStatus.WARNING
        );
    }

    @Test
    public void shouldCreateNotAvailableWidgetWhenModuleMissing() {
        DashboardSummary summary =
                new DashboardAggregator().aggregate(
                        List.of()
                );

        DashboardSection section =
                new PerformanceSectionBuilder().build(summary);

        Assert.assertEquals(
                section.getVisibleWidgets().size(),
                1
        );

        DashboardWidget widget =
                section.getVisibleWidgets().get(0);

        Assert.assertEquals(
                widget.getId(),
                "performance-module"
        );

        Assert.assertEquals(
                widget.getStatus(),
                WidgetStatus.NOT_AVAILABLE
        );

        Assert.assertEquals(
                widget.getData().get("available"),
                false
        );
    }

    @Test
    public void shouldIgnoreModuleNameCase() {
        ExecutionSummary performance =
                createExecutionSummary(
                        "PERFORMANCE",
                        ExecutionStatus.PASS,
                        80,
                        80,
                        0,
                        0
                );

        DashboardSummary summary =
                new DashboardAggregator().aggregate(
                        List.of(performance)
                );

        DashboardSection section =
                new PerformanceSectionBuilder().build(summary);

        Assert.assertEquals(
                section.getVisibleWidgets().size(),
                4
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Dashboard summary must not be null"
    )
    public void shouldRejectNullDashboardSummary() {
        new PerformanceSectionBuilder().build(null);
    }

    private DashboardWidget findWidget(
            DashboardSection section,
            String widgetId
    ) {
        return section.getVisibleWidgets()
                .stream()
                .filter(widget ->
                        widgetId.equals(widget.getId())
                )
                .findFirst()
                .orElseThrow();
    }

    private DashboardSummary createDashboardSummary(
            ExecutionStatus status,
            int total,
            int passed,
            int failed,
            int skipped
    ) {
        ExecutionSummary performance =
                createExecutionSummary(
                        "Performance",
                        status,
                        total,
                        passed,
                        failed,
                        skipped
                );

        return new DashboardAggregator().aggregate(
                List.of(performance)
        );
    }

    private ExecutionSummary createExecutionSummary(
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
                        30
                ),
                new ExecutionEnvironment(
                        "QA",
                        "108",
                        "sprint-2-unified-dashboard",
                        "local",
                        "performance-001"
                )
        );
    }
}
