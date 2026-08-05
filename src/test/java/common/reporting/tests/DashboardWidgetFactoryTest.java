package common.reporting.tests;

import common.reporting.dashboard.widget.DashboardWidget;
import common.reporting.dashboard.widget.DashboardWidgetFactory;
import common.reporting.dashboard.widget.WidgetStatus;
import common.reporting.dashboard.widget.WidgetType;
import common.reporting.model.ExecutionStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DashboardWidgetFactoryTest {

    private DashboardWidgetFactory widgetFactory;

    @BeforeMethod
    public void setUp() {
        widgetFactory = new DashboardWidgetFactory();
    }

    @Test
    public void shouldCreateStatusWidget() {
        DashboardWidget widget =
                widgetFactory.createStatusWidget(
                        "overall-status",
                        "Overall Status",
                        ExecutionStatus.PASS,
                        1
                );

        Assert.assertEquals(
                widget.getId(),
                "overall-status"
        );

        Assert.assertEquals(
                widget.getTitle(),
                "Overall Status"
        );

        Assert.assertEquals(
                widget.getType(),
                WidgetType.STATUS
        );

        Assert.assertEquals(
                widget.getStatus(),
                WidgetStatus.SUCCESS
        );

        Assert.assertEquals(
                widget.getData().get("value"),
                "PASS"
        );

        Assert.assertEquals(
                widget.getDisplayOrder(),
                1
        );
    }

    @Test
    public void shouldMapFailedStatusWidget() {
        DashboardWidget widget =
                widgetFactory.createStatusWidget(
                        "overall-status",
                        "Overall Status",
                        ExecutionStatus.FAIL,
                        1
                );

        Assert.assertEquals(
                widget.getStatus(),
                WidgetStatus.FAILURE
        );
    }

    @Test
    public void shouldCreateKpiWidget() {
        DashboardWidget widget =
                widgetFactory.createKpiWidget(
                        "total-tests",
                        "Total Tests",
                        25,
                        2
                );

        Assert.assertEquals(
                widget.getType(),
                WidgetType.KPI
        );

        Assert.assertEquals(
                widget.getStatus(),
                WidgetStatus.INFORMATION
        );

        Assert.assertEquals(
                widget.getData().get("value"),
                25
        );
    }

    @Test
    public void shouldCreateKpiWidgetWithUnit() {
        DashboardWidget widget =
                widgetFactory.createKpiWidget(
                        "pass-rate",
                        "Pass Rate",
                        95.5,
                        "%",
                        3
                );

        Assert.assertEquals(
                widget.getData().get("value"),
                95.5
        );

        Assert.assertEquals(
                widget.getData().get("unit"),
                "%"
        );
    }

    @Test
    public void shouldCreateKpiWidgetWithCustomStatus() {
        DashboardWidget widget =
                widgetFactory.createKpiWidget(
                        "failed-tests",
                        "Failed",
                        2,
                        WidgetStatus.FAILURE,
                        4
                );

        Assert.assertEquals(
                widget.getStatus(),
                WidgetStatus.FAILURE
        );
    }

    @Test
    public void shouldCreateModuleWidget() {
        DashboardWidget widget =
                widgetFactory.createModuleWidget(
                        "api-module",
                        "API",
                        ExecutionStatus.PARTIAL,
                        10,
                        8,
                        1,
                        1,
                        1
                );

        Assert.assertEquals(
                widget.getType(),
                WidgetType.MODULE
        );

        Assert.assertEquals(
                widget.getStatus(),
                WidgetStatus.WARNING
        );

        Assert.assertEquals(
                widget.getData().get("total"),
                10
        );

        Assert.assertEquals(
                widget.getData().get("failed"),
                1
        );
    }

    @Test
    public void shouldCreateEnvironmentWidget() {
        DashboardWidget widget =
                widgetFactory.createEnvironmentWidget(
                        "execution-environment",
                        "Execution Environment",
                        "QA",
                        "105",
                        "sprint-2-unified-dashboard",
                        "local",
                        "run-001",
                        1
                );

        Assert.assertEquals(
                widget.getType(),
                WidgetType.ENVIRONMENT
        );

        Assert.assertEquals(
                widget.getData().get("environment"),
                "QA"
        );

        Assert.assertEquals(
                widget.getData().get("buildNumber"),
                "105"
        );

        Assert.assertEquals(
                widget.getData().get("executionId"),
                "run-001"
        );
    }

    @Test
    public void shouldCreateAvailableDownloadWidget() {
        DashboardWidget widget =
                widgetFactory.createDownloadWidget(
                        "api-summary-download",
                        "API Summary",
                        "Download API summary",
                        "api/reports/summary.json",
                        1
                );

        Assert.assertEquals(
                widget.getType(),
                WidgetType.DOWNLOAD
        );

        Assert.assertEquals(
                widget.getStatus(),
                WidgetStatus.INFORMATION
        );

        Assert.assertEquals(
                widget.getData().get("available"),
                true
        );

        Assert.assertEquals(
                widget.getData().get("path"),
                "api/reports/summary.json"
        );
    }

    @Test
    public void shouldCreateUnavailableDownloadWidget() {
        DashboardWidget widget =
                widgetFactory.createDownloadWidget(
                        "api-summary-download",
                        "API Summary",
                        "Download API summary",
                        null,
                        1
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
    public void shouldCreateNotAvailableWidget() {
        DashboardWidget widget =
                widgetFactory.createNotAvailableWidget(
                        "performance-module",
                        "Performance",
                        WidgetType.MODULE,
                        1
                );

        Assert.assertEquals(
                widget.getStatus(),
                WidgetStatus.NOT_AVAILABLE
        );

        Assert.assertEquals(
                widget.getData().get("available"),
                false
        );

        Assert.assertEquals(
                widget.getData().get("message"),
                "Data not available"
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Widget id must not be blank"
    )
    public void shouldRejectBlankWidgetId() {
        widgetFactory.createKpiWidget(
                " ",
                "Total Tests",
                10,
                1
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Widget title must not be blank"
    )
    public void shouldRejectBlankWidgetTitle() {
        widgetFactory.createKpiWidget(
                "total-tests",
                null,
                10,
                1
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Execution status must not be null"
    )
    public void shouldRejectNullExecutionStatus() {
        widgetFactory.createStatusWidget(
                "overall-status",
                "Overall Status",
                null,
                1
        );
    }
}
