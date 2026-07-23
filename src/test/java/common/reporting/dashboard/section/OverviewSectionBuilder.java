package common.reporting.dashboard.section;

import common.reporting.dashboard.DashboardSummary;
import common.reporting.dashboard.widget.DashboardSection;
import common.reporting.dashboard.widget.DashboardWidget;
import common.reporting.dashboard.widget.WidgetStatus;
import common.reporting.dashboard.widget.WidgetType;

public class OverviewSectionBuilder
        extends AbstractDashboardSectionBuilder {

    @Override
    public String getSectionId() {
        return "overview";
    }

    @Override
    public int getDisplayOrder() {
        return 1;
    }

    @Override
    public DashboardSection build(DashboardSummary summary) {
        if (summary == null) {
            throw new IllegalArgumentException(
                    "Dashboard summary must not be null"
            );
        }

        DashboardSection section = new DashboardSection(
                getSectionId(),
                "Overview",
                getDisplayOrder()
        );

        section.addWidget(buildOverallStatusWidget(summary));
        section.addWidget(createKpiWidget(
                "total-tests",
                "Total Tests",
                summary.getMetrics().getTotalTests(),
                2
        ));

        DashboardWidget passRate = createKpiWidget(
                "pass-rate",
                "Pass Rate",
                summary.getMetrics().getPassRate(),
                3
        );
        passRate.addData("unit", "%");
        section.addWidget(passRate);

        DashboardWidget duration = createKpiWidget(
                "duration",
                "Duration",
                summary.getMetrics().getDurationSeconds(),
                4
        );
        duration.addData("unit", "seconds");
        section.addWidget(duration);

        section.addWidget(createKpiWidget(
                "module-count",
                "Modules",
                summary.getModules().size(),
                5
        ));

        section.addWidget(createKpiWidget(
                "passed-tests",
                "Passed",
                summary.getMetrics().getPassed(),
                6
        ));

        section.addWidget(createKpiWidget(
                "failed-tests",
                "Failed",
                summary.getMetrics().getFailed(),
                7
        ));

        section.addWidget(createKpiWidget(
                "skipped-tests",
                "Skipped",
                summary.getMetrics().getSkipped(),
                8
        ));

        return section;
    }

    private DashboardWidget buildOverallStatusWidget(
            DashboardSummary summary
    ) {
        DashboardWidget widget = new DashboardWidget(
                "overall-status",
                "Overall Status",
                WidgetType.STATUS
        );

        widget.setDisplayOrder(1);
        widget.setStatus(mapStatus(summary.getOverallStatus()));
        widget.addData(
                "value",
                summary.getOverallStatus().name()
        );

        return widget;
    }

    private DashboardWidget createKpiWidget(
            String id,
            String title,
            Object value,
            int displayOrder
    ) {
        DashboardWidget widget = new DashboardWidget(
                id,
                title,
                WidgetType.KPI
        );

        widget.setDisplayOrder(displayOrder);
        widget.setStatus(resolveKpiStatus(id, value));
        widget.addData("value", value);

        return widget;
    }

    private WidgetStatus resolveKpiStatus(
            String widgetId,
            Object value
    ) {
        if ("failed-tests".equals(widgetId)
                && value instanceof Number number
                && number.intValue() > 0) {
            return WidgetStatus.FAILURE;
        }

        if ("skipped-tests".equals(widgetId)
                && value instanceof Number number
                && number.intValue() > 0) {
            return WidgetStatus.WARNING;
        }

        return WidgetStatus.INFORMATION;
    }
}
