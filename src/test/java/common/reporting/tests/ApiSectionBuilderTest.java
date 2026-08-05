package common.reporting.tests;

import common.reporting.aggregation.DashboardAggregator;
import common.reporting.dashboard.DashboardSummary;
import common.reporting.dashboard.section.ApiSectionBuilder;
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

public class ApiSectionBuilderTest {

    @Test
    public void shouldBuildApiSection() {

        DashboardSection section =
                new ApiSectionBuilder().build(createDashboardSummary());

        Assert.assertEquals(section.getId(), "api");
        Assert.assertEquals(section.getTitle(), "API");
        Assert.assertEquals(section.getVisibleWidgets().size(), 4);
    }

    @Test
    public void shouldCreateNotAvailableWidgetWhenApiModuleMissing() {

        DashboardSummary summary =
                new DashboardAggregator().aggregate(List.of());

        DashboardSection section =
                new ApiSectionBuilder().build(summary);

        Assert.assertEquals(section.getVisibleWidgets().size(), 1);

        DashboardWidget widget =
                section.getVisibleWidgets().get(0);

        Assert.assertEquals(
                widget.getStatus(),
                WidgetStatus.NOT_AVAILABLE
        );
    }

    @Test
    public void shouldPopulateApiWidgets() {

        DashboardSection section =
                new ApiSectionBuilder().build(createDashboardSummary());

        DashboardWidget status =
                find(section, "api-status");

        DashboardWidget module =
                find(section, "api-module");

        Assert.assertEquals(
                status.getStatus(),
                WidgetStatus.SUCCESS
        );

        Assert.assertEquals(
                module.getData().get("passed"),
                9
        );

        Assert.assertEquals(
                module.getData().get("failed"),
                0
        );
    }

    private DashboardWidget find(
            DashboardSection section,
            String id
    ) {

        return section.getVisibleWidgets()
                .stream()
                .filter(widget ->
                        id.equals(widget.getId()))
                .findFirst()
                .orElseThrow();
    }

    private DashboardSummary createDashboardSummary() {

        ExecutionSummary api =
                new ExecutionSummary(
                        "API",
                        ExecutionStatus.PASS,
                        ExecutionMetrics.fromCounts(
                                9,
                                9,
                                0,
                                0,
                                12
                        ),
                        new ExecutionEnvironment(
                                "QA",
                                "105",
                                "main",
                                "local",
                                "run-001"
                        )
                );

        return new DashboardAggregator()
                .aggregate(List.of(api));
    }
}
