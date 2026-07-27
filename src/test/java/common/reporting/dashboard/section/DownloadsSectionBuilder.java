package common.reporting.dashboard.section;

import common.reporting.dashboard.DashboardSummary;
import common.reporting.dashboard.widget.DashboardSection;
import common.reporting.dashboard.widget.DashboardWidgetFactory;

public class DownloadsSectionBuilder
        extends AbstractDashboardSectionBuilder {

    private static final String API_SUMMARY_PATH =
            "api/reports/summary.json";

    private static final String PERFORMANCE_SUMMARY_PATH =
            "performance/reports/summary.json";

    private static final String DASHBOARD_HTML_PATH =
            "dashboard/reports/index.html";

    private final DashboardWidgetFactory widgetFactory;

    public DownloadsSectionBuilder() {
        this(new DashboardWidgetFactory());
    }

    DownloadsSectionBuilder(
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
        return "downloads";
    }

    @Override
    public int getDisplayOrder() {
        return 5;
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
                "Downloads",
                getDisplayOrder()
        );

        section.addWidget(
                widgetFactory.createDownloadWidget(
                        "api-summary-download",
                        "API Summary",
                        "Download API summary",
                        resolveApiSummaryPath(summary),
                        1
                )
        );

        section.addWidget(
                widgetFactory.createDownloadWidget(
                        "performance-summary-download",
                        "Performance Summary",
                        "Download performance summary",
                        resolvePerformanceSummaryPath(summary),
                        2
                )
        );

        section.addWidget(
                widgetFactory.createDownloadWidget(
                        "dashboard-html-download",
                        "Dashboard HTML",
                        "Open dashboard report",
                        DASHBOARD_HTML_PATH,
                        3
                )
        );

        return section;
    }

    private String resolveApiSummaryPath(
            DashboardSummary summary
    ) {
        return hasModule(summary, "API")
                ? API_SUMMARY_PATH
                : null;
    }

    private String resolvePerformanceSummaryPath(
            DashboardSummary summary
    ) {
        return hasModule(summary, "Performance")
                ? PERFORMANCE_SUMMARY_PATH
                : null;
    }

    private boolean hasModule(
            DashboardSummary summary,
            String moduleName
    ) {
        if (summary.getModules() == null) {
            return false;
        }

        return summary.getModules()
                .stream()
                .filter(module -> module != null)
                .anyMatch(module ->
                        module.getModule() != null
                                && moduleName.equalsIgnoreCase(
                                        module.getModule()
                                )
                );
    }
}
