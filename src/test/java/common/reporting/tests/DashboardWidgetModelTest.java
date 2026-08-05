package common.reporting.tests;

import common.reporting.dashboard.widget.DashboardSection;
import common.reporting.dashboard.widget.DashboardWidget;
import common.reporting.dashboard.widget.WidgetStatus;
import common.reporting.dashboard.widget.WidgetType;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DashboardWidgetModelTest {

    @Test
    public void shouldCreateDashboardWidget() {
        DashboardWidget widget = new DashboardWidget(
                "overall-status",
                "Overall Status",
                WidgetType.STATUS
        );

        widget.setStatus(WidgetStatus.SUCCESS);
        widget.setDisplayOrder(1);
        widget.addData("value", "PASS");
        widget.addLink("details", "reports/details.html");

        Assert.assertEquals(widget.getId(), "overall-status");
        Assert.assertEquals(widget.getTitle(), "Overall Status");
        Assert.assertEquals(widget.getType(), WidgetType.STATUS);
        Assert.assertEquals(
                widget.getStatus(),
                WidgetStatus.SUCCESS
        );
        Assert.assertEquals(widget.getData().get("value"), "PASS");
        Assert.assertEquals(
                widget.getLinks().get("details"),
                "reports/details.html"
        );
    }

    @Test
    public void shouldReturnVisibleWidgetsInDisplayOrder() {
        DashboardWidget second = new DashboardWidget(
                "second",
                "Second",
                WidgetType.KPI
        );
        second.setDisplayOrder(2);

        DashboardWidget first = new DashboardWidget(
                "first",
                "First",
                WidgetType.KPI
        );
        first.setDisplayOrder(1);

        DashboardWidget hidden = new DashboardWidget(
                "hidden",
                "Hidden",
                WidgetType.TEXT
        );
        hidden.setDisplayOrder(0);
        hidden.setVisible(false);

        DashboardSection section = new DashboardSection(
                "overview",
                "Overview",
                1
        );

        section.addWidget(second);
        section.addWidget(hidden);
        section.addWidget(first);

        Assert.assertEquals(
                section.getVisibleWidgets().size(),
                2
        );

        Assert.assertEquals(
                section.getVisibleWidgets().get(0).getId(),
                "first"
        );

        Assert.assertEquals(
                section.getVisibleWidgets().get(1).getId(),
                "second"
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Widget title must not be blank"
    )
    public void shouldRejectBlankWidgetTitle() {
        new DashboardWidget(
                "status",
                " ",
                WidgetType.STATUS
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Dashboard widget must not be null"
    )
    public void shouldRejectNullWidgetInSection() {
        DashboardSection section = new DashboardSection(
                "overview",
                "Overview",
                1
        );

        section.addWidget(null);
    }
}
