package common.reporting.tests;

import common.reporting.aggregation.DashboardAggregator;
import common.reporting.dashboard.DashboardSummary;
import common.reporting.dashboard.section.OverviewSectionBuilder;
import common.reporting.dashboard.widget.DashboardSection;
import common.reporting.dashboard.widget.DashboardWidget;
import common.reporting.dashboard.widget.WidgetStatus;
import common.reporting.model.ExecutionEnvironment;
import common.reporting.model.ExecutionMetrics;
import common.reporting.model.ExecutionStatus;
import common.reporting.model.ExecutionSummary;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class OverviewSectionBuilderTest {

    @Test
    public void shouldBuildOverviewSection() {
        DashboardSummary summary = createSummary(
                ExecutionStatus.PASS,
                10,
                10,
                0,
                0
        );

        DashboardSection section =
                new OverviewSectionBuilder().build(summary);

        Assert.assertEquals(section.getId(), "overview");
        Assert.assertEquals(section.getTitle(), "Overview");
        Assert.assertEquals(section.getDisplayOrder(), 1);
        Assert.assertEquals(
                section.getVisibleWidgets().size(),
                8
        );
    }

    @Test
    public void shouldPopulateOverviewKpis() {
        DashboardSummary summary = createSummary(
                ExecutionStatus.PARTIAL,
                12,
                10,
                1,
                1
        );

        DashboardSection section =
                new OverviewSectionBuilder().build(summary);

        Assert.assertEquals(
                findWidget(section, "overall-status")
                        .getData().get("value"),
                "PARTIAL"
        );

        Assert.assertEquals(
                findWidget(section, "total-tests")
                        .getData().get("value"),
                12
        );

        Assert.assertEquals(
                findWidget(section, "passed-tests")
                        .getData().get("value"),
                10
        );

        Assert.assertEquals(
                findWidget(section, "failed-tests")
                        .getData().get("value"),
                1
        );

        Assert.assertEquals(
                findWidget(section, "skipped-tests")
                        .getData().get("value"),
                1
        );
    }

    @Test
    public void shouldMapFailedTestsToFailureStatus() {
        DashboardSummary summary = createSummary(
                ExecutionStatus.FAIL,
                5,
                4,
                1,
                0
        );

        DashboardSection section =
                new OverviewSectionBuilder().build(summary);

        Assert.assertEquals(
                findWidget(section, "failed-tests").getStatus(),
                WidgetStatus.FAILURE
        );

        Assert.assertEquals(
                findWidget(section, "overall-status").getStatus(),
                WidgetStatus.FAILURE
        );
    }

    @Test
    public void shouldMapSkippedTestsToWarningStatus() {
        DashboardSummary summary = createSummary(
                ExecutionStatus.PARTIAL,
                5,
                4,
                0,
                1
        );

        DashboardSection section =
                new OverviewSectionBuilder().build(summary);

        Assert.assertEquals(
                findWidget(section, "skipped-tests").getStatus(),
                WidgetStatus.WARNING
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Dashboard summary must not be null"
    )
    public void shouldRejectNullDashboardSummary() {
        new OverviewSectionBuilder().build(null);
    }

    private DashboardWidget findWidget(
            DashboardSection section,
            String widgetId
    ) {
        return section.getVisibleWidgets().stream()
                .filter(widget ->
                        widgetId.equals(widget.getId())
                )
                .findFirst()
                .orElseThrow();
    }

    private DashboardSummary createSummary(
            ExecutionStatus status,
            int total,
            int passed,
            int failed,
            int skipped
    ) {
        ExecutionSummary api = new ExecutionSummary(
                "API",
                status,
                ExecutionMetrics.fromCounts(
                        total,
                        passed,
                        failed,
                        skipped,
                        10
                ),
                new ExecutionEnvironment(
                        "QA",
                        "100",
                        "sprint-2-unified-dashboard",
                        "local",
                        "api-001"
                )
        );

        return new DashboardAggregator().aggregate(
                List.of(api)
        );
    }
}
