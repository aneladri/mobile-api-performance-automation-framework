package common.reporting.dashboard.section;

import common.reporting.dashboard.DashboardModule;
import common.reporting.dashboard.DashboardSummary;
import common.reporting.dashboard.widget.DashboardSection;
import common.reporting.dashboard.widget.DashboardWidgetFactory;
import common.reporting.dashboard.widget.WidgetType;
import common.reporting.model.ExecutionMetrics;
import common.reporting.model.ExecutionSummary;

public class WebSectionBuilder
        extends AbstractDashboardSectionBuilder {

    private static final String MODULE_NAME = "Web";

    private final DashboardWidgetFactory widgetFactory;

    public WebSectionBuilder() {
        this(new DashboardWidgetFactory());
    }

    WebSectionBuilder(
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
        return "web";
    }

    @Override
    public int getDisplayOrder() {
        return 3;
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

        DashboardSection section =
                new DashboardSection(
                        getSectionId(),
                        "Web",
                        getDisplayOrder()
                );

        DashboardModule module =
                summary.getModules()
                        .stream()
                        .filter(currentModule ->
                                MODULE_NAME.equalsIgnoreCase(
                                        currentModule.getModule()
                                )
                        )
                        .findFirst()
                        .orElse(null);

        if (module == null) {
            section.addWidget(
                    widgetFactory.createNotAvailableWidget(
                            "web-module",
                            "Web",
                            WidgetType.MODULE,
                            1
                    )
            );

            return section;
        }

        ExecutionSummary executionSummary =
                module.getSummary();

        ExecutionMetrics metrics =
                executionSummary.getMetrics();

        section.addWidget(
                widgetFactory.createStatusWidget(
                        "web-status",
                        "Web Status",
                        executionSummary.getStatus(),
                        1
                )
        );

        section.addWidget(
                widgetFactory.createModuleWidget(
                        "web-module",
                        "Web Summary",
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
                        "web-pass-rate",
                        "Pass Rate",
                        metrics.getPassRate(),
                        "%",
                        3
                )
        );

        section.addWidget(
                widgetFactory.createKpiWidget(
                        "web-duration",
                        "Duration",
                        metrics.getDurationSeconds(),
                        "seconds",
                        4
                )
        );

        return section;
    }
}