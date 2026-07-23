package common.reporting.dashboard.section;

import common.reporting.dashboard.DashboardModule;
import common.reporting.dashboard.DashboardSummary;
import common.reporting.dashboard.widget.WidgetStatus;
import common.reporting.model.ExecutionStatus;

public abstract class AbstractDashboardSectionBuilder
        implements DashboardSectionBuilder {

    protected DashboardModule findModule(
            DashboardSummary summary,
            String moduleName
    ) {
        if (summary == null || moduleName == null) {
            return null;
        }

        return summary.getModules().stream()
                .filter(module -> module != null)
                .filter(module -> module.getModule() != null)
                .filter(module -> module.getModule()
                        .equalsIgnoreCase(moduleName))
                .findFirst()
                .orElse(null);
    }

    protected WidgetStatus mapStatus(
            ExecutionStatus executionStatus
    ) {
        if (executionStatus == null) {
            return WidgetStatus.NOT_AVAILABLE;
        }

        return switch (executionStatus) {
            case PASS -> WidgetStatus.SUCCESS;
            case FAIL -> WidgetStatus.FAILURE;
            case PARTIAL, SKIPPED -> WidgetStatus.WARNING;
            case NOT_RUN -> WidgetStatus.NOT_AVAILABLE;
        };
    }
}
