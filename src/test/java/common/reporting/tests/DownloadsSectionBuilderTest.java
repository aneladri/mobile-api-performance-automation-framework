package common.reporting.tests;

import common.reporting.aggregation.DashboardAggregator;
import common.reporting.dashboard.DashboardSummary;
import common.reporting.dashboard.section.DownloadsSectionBuilder;
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

public class DownloadsSectionBuilderTest {

    @Test
    public void shouldBuildDownloadsSection() {
        DashboardSection section =
                new DownloadsSectionBuilder().build(
                        createDashboardSummary()
                );

        Assert.assertEquals(
                section.getId(),
                "downloads"
        );

        Assert.assertEquals(
                section.getTitle(),
                "Downloads"
        );

        Assert.assertEquals(
                section.getDisplayOrder(),
                5
        );

        Assert.assertEquals(
                section.getVisibleWidgets().size(),
                3
        );
    }

    @Test
    public void shouldCreateAvailableDownloadWidgets() {
        DashboardSection section =
                new DownloadsSectionBuilder().build(
                        createDashboardSummary()
                );

        DashboardWidget apiDownload =
                findWidget(
                        section,
                        "api-summary-download"
                );

        DashboardWidget performanceDownload =
                findWidget(
                        section,
                        "performance-summary-download"
                );

        DashboardWidget dashboardDownload =
                findWidget(
                        section,
                        "dashboard-html-download"
                );

        Assert.assertEquals(
                apiDownload.getType(),
                WidgetType.DOWNLOAD
        );

        Assert.assertEquals(
                apiDownload.getStatus(),
                WidgetStatus.INFORMATION
        );

        Assert.assertEquals(
                apiDownload.getData().get("available"),
                true
        );

        Assert.assertEquals(
                apiDownload.getData().get("path"),
                "api/reports/summary.json"
        );

        Assert.assertEquals(
                performanceDownload.getData().get("path"),
                "performance/reports/summary.json"
        );

        Assert.assertEquals(
                dashboardDownload.getData().get("path"),
                "dashboard/reports/index.html"
        );
    }

    @Test
    public void shouldMarkApiDownloadUnavailableWhenApiModuleMissing() {
        DashboardSummary summary =
                new DashboardAggregator().aggregate(
                        List.of(
                                createExecutionSummary(
                                        "Performance"
                                )
                        )
                );

        DashboardSection section =
                new DownloadsSectionBuilder().build(summary);

        DashboardWidget apiDownload =
                findWidget(
                        section,
                        "api-summary-download"
                );

        Assert.assertEquals(
                apiDownload.getStatus(),
                WidgetStatus.NOT_AVAILABLE
        );

        Assert.assertEquals(
                apiDownload.getData().get("available"),
                false
        );
    }

    @Test
    public void shouldMarkPerformanceDownloadUnavailableWhenModuleMissing() {
        DashboardSummary summary =
                new DashboardAggregator().aggregate(
                        List.of(
                                createExecutionSummary("API")
                        )
                );

        DashboardSection section =
                new DownloadsSectionBuilder().build(summary);

        DashboardWidget performanceDownload =
                findWidget(
                        section,
                        "performance-summary-download"
                );

        Assert.assertEquals(
                performanceDownload.getStatus(),
                WidgetStatus.NOT_AVAILABLE
        );

        Assert.assertEquals(
                performanceDownload.getData().get("available"),
                false
        );
    }

    @Test
    public void shouldKeepDashboardHtmlDownloadAvailableForEmptySummary() {
        DashboardSummary summary =
                new DashboardAggregator().aggregate(
                        List.of()
                );

        DashboardSection section =
                new DownloadsSectionBuilder().build(summary);

        DashboardWidget dashboardDownload =
                findWidget(
                        section,
                        "dashboard-html-download"
                );

        Assert.assertEquals(
                dashboardDownload.getStatus(),
                WidgetStatus.INFORMATION
        );

        Assert.assertEquals(
                dashboardDownload.getData().get("available"),
                true
        );
    }

    @Test
    public void shouldIgnoreModuleNameCase() {
        DashboardSummary summary =
                new DashboardAggregator().aggregate(
                        List.of(
                                createExecutionSummary("api"),
                                createExecutionSummary(
                                        "PERFORMANCE"
                                )
                        )
                );

        DashboardSection section =
                new DownloadsSectionBuilder().build(summary);

        Assert.assertEquals(
                findWidget(
                        section,
                        "api-summary-download"
                ).getData().get("available"),
                true
        );

        Assert.assertEquals(
                findWidget(
                        section,
                        "performance-summary-download"
                ).getData().get("available"),
                true
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Dashboard summary must not be null"
    )
    public void shouldRejectNullDashboardSummary() {
        new DownloadsSectionBuilder().build(null);
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

    private DashboardSummary createDashboardSummary() {
        return new DashboardAggregator().aggregate(
                List.of(
                        createExecutionSummary("API"),
                        createExecutionSummary("Performance")
                )
        );
    }

    private ExecutionSummary createExecutionSummary(
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
                        10
                ),
                new ExecutionEnvironment(
                        "QA",
                        "110",
                        "sprint-2-unified-dashboard",
                        "abc123",
                        module.toLowerCase() + "-001"
                )
        );
    }
}
