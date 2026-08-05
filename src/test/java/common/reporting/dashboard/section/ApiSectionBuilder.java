package common.reporting.dashboard.section;

import common.reporting.dashboard.DashboardModule;
import common.reporting.dashboard.DashboardSummary;
import common.reporting.dashboard.widget.DashboardSection;
import common.reporting.dashboard.widget.DashboardWidgetFactory;
import common.reporting.dashboard.widget.WidgetType;
import common.reporting.model.ExecutionMetrics;
import common.reporting.model.ExecutionSummary;

public class ApiSectionBuilder extends AbstractDashboardSectionBuilder {

    private static final String MODULE_NAME = "API";

    private final DashboardWidgetFactory widgetFactory;

    public ApiSectionBuilder() {
        this(new DashboardWidgetFactory());
    }

    ApiSectionBuilder(
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
        return "api";
    }

    @Override
    public int getDisplayOrder() {
        return 2;
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
                "API",
                getDisplayOrder()
        );

        DashboardModule module = summary.getModules()
                .stream()
                .filter(m -> MODULE_NAME.equalsIgnoreCase(m.getModule()))
                .findFirst()
                .orElse(null);

        if (module == null) {

            section.addWidget(
                    widgetFactory.createNotAvailableWidget(
                            "api-module",
                            "API",
                            WidgetType.MODULE,
                            1
                    )
            );

            return section;
        }

        ExecutionSummary executionSummary = module.getSummary();
        ExecutionMetrics metrics = executionSummary.getMetrics();

        section.addWidget(
                widgetFactory.createStatusWidget(
                        "api-status",
                        "API Status",
                        executionSummary.getStatus(),
                        1
                )
        );

        section.addWidget(
                widgetFactory.createModuleWidget(
                        "api-module",
                        "API Summary",
                        executionSummary.getStatus(),
                        metrics.getTotal(),
                        metrics.getPassed(),
                        metrics.getFailed(),
                        metrics.getSkipped(),
                        2
                )
        );

        section.addWidget(
                widgetFactory.createKpiWidget(
                        "api-pass-rate",
                        "Pass Rate",
                        metrics.getPassRate(),
                        "%",
                        3
                )
        );

        section.addWidget(
                widgetFactory.createKpiWidget(
                        "api-duration",
                        "Duration",
                        metrics.getDurationSeconds(),
                        "seconds",
                        4
                )
        );

        return section;
    }
}
