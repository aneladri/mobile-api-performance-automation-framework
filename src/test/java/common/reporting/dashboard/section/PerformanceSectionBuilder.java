package common.reporting.dashboard.section;

import common.reporting.dashboard.DashboardModule;
import common.reporting.dashboard.DashboardSummary;
import common.reporting.dashboard.widget.DashboardSection;
import common.reporting.dashboard.widget.DashboardWidgetFactory;
import common.reporting.dashboard.widget.WidgetType;
import common.reporting.model.ExecutionMetrics;
import common.reporting.model.ExecutionSummary;

public class PerformanceSectionBuilder
        extends AbstractDashboardSectionBuilder {

    private static final String MODULE_NAME = "Performance";

    private final DashboardWidgetFactory widgetFactory;

    public PerformanceSectionBuilder() {
        this(new DashboardWidgetFactory());
    }

    PerformanceSectionBuilder(
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
        return "performance";
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

        DashboardSection section = new DashboardSection(
                getSectionId(),
                "Performance",
                getDisplayOrder()
        );

        DashboardModule module = findPerformanceModule(summary);

        if (module == null || module.getSummary() == null) {
            section.addWidget(
                    widgetFactory.createNotAvailableWidget(
                            "performance-module",
                            "Performance",
                            WidgetType.MODULE,
                            1
                    )
            );

            return section;
        }

        ExecutionSummary executionSummary = module.getSummary();
        ExecutionMetrics metrics = executionSummary.getMetrics();

        if (metrics == null) {
            section.addWidget(
                    widgetFactory.createNotAvailableWidget(
                            "performance-module",
                            "Performance",
                            WidgetType.MODULE,
                            1
                    )
            );

            return section;
        }

        section.addWidget(
                widgetFactory.createStatusWidget(
                        "performance-status",
                        "Performance Status",
                        executionSummary.getStatus(),
                        1
                )
        );

        section.addWidget(
                widgetFactory.createModuleWidget(
                        "performance-module",
                        "Performance Summary",
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
                        "performance-pass-rate",
                        "Pass Rate",
                        metrics.getPassRate(),
                        "%",
                        3
                )
        );

        section.addWidget(
                widgetFactory.createKpiWidget(
                        "performance-duration",
                        "Duration",
                        metrics.getDurationSeconds(),
                        "seconds",
                        4
                )
        );

        return section;
    }

    private DashboardModule findPerformanceModule(
            DashboardSummary summary
    ) {
        if (summary.getModules() == null) {
            return null;
        }

        return summary.getModules()
                .stream()
                .filter(module -> module != null)
                .filter(module -> module.getModule() != null)
                .filter(module ->
                        MODULE_NAME.equalsIgnoreCase(
                                module.getModule()
                        )
                )
                .findFirst()
                .orElse(null);
    }
}
