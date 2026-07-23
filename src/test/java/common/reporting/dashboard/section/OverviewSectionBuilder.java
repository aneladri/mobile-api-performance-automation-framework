package common.reporting.dashboard.section;

import common.reporting.dashboard.DashboardSummary;
import common.reporting.dashboard.widget.DashboardSection;
import common.reporting.dashboard.widget.DashboardWidget;
import common.reporting.dashboard.widget.DashboardWidgetFactory;
import common.reporting.dashboard.widget.WidgetStatus;

public class OverviewSectionBuilder
        extends AbstractDashboardSectionBuilder {

    private final DashboardWidgetFactory widgetFactory;

    public OverviewSectionBuilder() {
        this(new DashboardWidgetFactory());
    }

    OverviewSectionBuilder(
            DashboardWidgetFactory widgetFactory
    ) {
        if (widgetFactory == null) {
            throw new IllegalArgumentException(
                    "Dashboard widget factory must not be null"
            );
        }

        this.widgetFactory = widgetFactory;
    }

    @Override
    public String getSectionId() {
        return "overview";
    }

    @Override
    public int getDisplayOrder() {
        return 1;
    }

    @Override
    public DashboardSection build(
            DashboardSummary summary
    ) {
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

        section.addWidget(
                widgetFactory.createStatusWidget(
                        "overall-status",
                        "Overall Status",
                        summary.getOverallStatus(),
                        1
                )
        );

        section.addWidget(
                widgetFactory.createKpiWidget(
                        "total-tests",
                        "Total Tests",
                        summary.getMetrics().getTotalTests(),
                        2
                )
        );

        section.addWidget(
                widgetFactory.createKpiWidget(
                        "pass-rate",
                        "Pass Rate",
                        summary.getMetrics().getPassRate(),
                        "%",
                        3
                )
        );

        section.addWidget(
                widgetFactory.createKpiWidget(
                        "duration",
                        "Duration",
                        summary.getMetrics().getDurationSeconds(),
                        "seconds",
                        4
                )
        );

        section.addWidget(
                widgetFactory.createKpiWidget(
                        "module-count",
                        "Modules",
                        summary.getModules().size(),
                        5
                )
        );

        section.addWidget(
                widgetFactory.createKpiWidget(
                        "passed-tests",
                        "Passed",
                        summary.getMetrics().getPassed(),
                        WidgetStatus.SUCCESS,
                        6
                )
        );

        section.addWidget(
                createFailedTestsWidget(summary)
        );

        section.addWidget(
                createSkippedTestsWidget(summary)
        );

        return section;
    }

    private DashboardWidget createFailedTestsWidget(
            DashboardSummary summary
    ) {
        int failed = summary.getMetrics().getFailed();

        WidgetStatus status = failed > 0
                ? WidgetStatus.FAILURE
                : WidgetStatus.SUCCESS;

        return widgetFactory.createKpiWidget(
                "failed-tests",
                "Failed",
                failed,
                status,
                7
        );
    }

    private DashboardWidget createSkippedTestsWidget(
            DashboardSummary summary
    ) {
        int skipped = summary.getMetrics().getSkipped();

        WidgetStatus status = skipped > 0
                ? WidgetStatus.WARNING
                : WidgetStatus.SUCCESS;

        return widgetFactory.createKpiWidget(
                "skipped-tests",
                "Skipped",
                skipped,
                status,
                8
        );
    }
}