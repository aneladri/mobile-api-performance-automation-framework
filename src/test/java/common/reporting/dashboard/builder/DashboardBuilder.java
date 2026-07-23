package common.reporting.dashboard.builder;

import common.reporting.dashboard.DashboardModule;
import common.reporting.dashboard.DashboardSummary;
import common.reporting.dashboard.config.DashboardConfiguration;
import common.reporting.dashboard.config.DashboardTab;
import common.reporting.dashboard.widget.DashboardSection;
import common.reporting.dashboard.widget.DashboardWidget;
import common.reporting.dashboard.widget.WidgetStatus;
import common.reporting.dashboard.widget.WidgetType;
import common.reporting.model.ExecutionEnvironment;
import common.reporting.model.ExecutionStatus;
import common.reporting.model.ExecutionSummary;

import java.util.Map;

public class DashboardBuilder {

    public DashboardPage build(
            DashboardSummary summary,
            DashboardConfiguration configuration
    ) {
        if (summary == null) {
            throw new IllegalArgumentException(
                    "Dashboard summary must not be null"
            );
        }

        if (configuration == null) {
            throw new IllegalArgumentException(
                    "Dashboard configuration must not be null"
            );
        }

        DashboardPage page = new DashboardPage(configuration);
        page.setGeneratedAt(summary.getGeneratedAt());

        if (configuration.isTabEnabled(DashboardTab.OVERVIEW)) {
            page.addSection(buildOverviewSection(summary));
        }

        if (configuration.isTabEnabled(DashboardTab.API)) {
            page.addSection(
                    buildModuleSection(
                            summary,
                            "API",
                            "api",
                            "API",
                            2
                    )
            );
        }

        if (configuration.isTabEnabled(
                DashboardTab.PERFORMANCE
        )) {
            page.addSection(
                    buildModuleSection(
                            summary,
                            "Performance",
                            "performance",
                            "Performance",
                            3
                    )
            );
        }

        if (configuration.isTabEnabled(
                DashboardTab.ENVIRONMENT
        )) {
            page.addSection(buildEnvironmentSection(summary));
        }

        if (configuration.isTabEnabled(
                DashboardTab.DOWNLOADS
        )) {
            page.addSection(buildDownloadsSection(summary));
        }

        return page;
    }

    private DashboardSection buildOverviewSection(
            DashboardSummary summary
    ) {
        DashboardSection section = new DashboardSection(
                "overview",
                "Overview",
                1
        );

        DashboardWidget overallStatus = new DashboardWidget(
                "overall-status",
                "Overall Status",
                WidgetType.STATUS
        );

        overallStatus.setDisplayOrder(1);
        overallStatus.setStatus(
                mapStatus(summary.getOverallStatus())
        );
        overallStatus.addData(
                "value",
                summary.getOverallStatus().name()
        );

        DashboardWidget totalTests = createKpiWidget(
                "total-tests",
                "Total Tests",
                summary.getMetrics().getTotalTests(),
                2
        );

        DashboardWidget passRate = createKpiWidget(
                "pass-rate",
                "Pass Rate",
                summary.getMetrics().getPassRate(),
                3
        );
        passRate.addData("unit", "%");

        DashboardWidget duration = createKpiWidget(
                "duration",
                "Duration",
                summary.getMetrics().getDurationSeconds(),
                4
        );
        duration.addData("unit", "seconds");

        DashboardWidget modules = createKpiWidget(
                "module-count",
                "Modules",
                summary.getModules().size(),
                5
        );

        section.addWidget(overallStatus);
        section.addWidget(totalTests);
        section.addWidget(passRate);
        section.addWidget(duration);
        section.addWidget(modules);

        return section;
    }

    private DashboardSection buildModuleSection(
            DashboardSummary dashboard,
            String moduleName,
            String sectionId,
            String sectionTitle,
            int displayOrder
    ) {
        DashboardSection section = new DashboardSection(
                sectionId,
                sectionTitle,
                displayOrder
        );

        DashboardModule module = findModule(
                dashboard,
                moduleName
        );

        if (module == null || module.getSummary() == null) {
            DashboardWidget notRun = new DashboardWidget(
                    sectionId + "-status",
                    sectionTitle + " Status",
                    WidgetType.MODULE
            );

            notRun.setDisplayOrder(1);
            notRun.setStatus(WidgetStatus.NOT_AVAILABLE);
            notRun.addData("value", "NOT_RUN");

            section.addWidget(notRun);
            return section;
        }

        ExecutionSummary summary = module.getSummary();

        DashboardWidget status = new DashboardWidget(
                sectionId + "-status",
                sectionTitle + " Status",
                WidgetType.MODULE
        );

        status.setDisplayOrder(1);
        status.setStatus(mapStatus(summary.getStatus()));
        status.addData("value", summary.getStatus().name());
        status.addData("total", summary.getMetrics().getTotal());
        status.addData("passed", summary.getMetrics().getPassed());
        status.addData("failed", summary.getMetrics().getFailed());
        status.addData("skipped", summary.getMetrics().getSkipped());
        status.addData(
                "passRate",
                summary.getMetrics().getPassRate()
        );
        status.addData(
                "durationSeconds",
                summary.getMetrics().getDurationSeconds()
        );

        for (Map.Entry<String, String> link
                : summary.getLinks().entrySet()) {
            status.addLink(link.getKey(), link.getValue());
        }

        section.addWidget(status);

        return section;
    }

    private DashboardSection buildEnvironmentSection(
            DashboardSummary summary
    ) {
        DashboardSection section = new DashboardSection(
                "environment",
                "Environment",
                4
        );

        DashboardWidget widget = new DashboardWidget(
                "execution-environment",
                "Execution Environment",
                WidgetType.ENVIRONMENT
        );

        widget.setDisplayOrder(1);
        widget.setStatus(WidgetStatus.INFORMATION);

        ExecutionEnvironment environment =
                summary.getEnvironment();

        if (environment == null) {
            widget.addData("environment", "Not available");
        } else {
            widget.addData(
                    "environment",
                    environment.getEnvironment()
            );
            widget.addData("build", environment.getBuild());
            widget.addData("branch", environment.getBranch());
            widget.addData("commit", environment.getCommit());
            widget.addData(
                    "executionId",
                    environment.getExecutionId()
            );
            widget.addData(
                    "generatedAt",
                    environment.getGeneratedAt()
            );
        }

        section.addWidget(widget);

        return section;
    }

    private DashboardSection buildDownloadsSection(
            DashboardSummary summary
    ) {
        DashboardSection section = new DashboardSection(
                "downloads",
                "Downloads",
                5
        );

        DashboardWidget downloads = new DashboardWidget(
                "report-downloads",
                "Available Reports",
                WidgetType.DOWNLOAD
        );

        downloads.setDisplayOrder(1);
        downloads.setStatus(WidgetStatus.INFORMATION);

        for (DashboardModule module : summary.getModules()) {
            if (module == null || module.getSummary() == null) {
                continue;
            }

            for (Map.Entry<String, String> link
                    : module.getSummary().getLinks().entrySet()) {
                downloads.addLink(
                        module.getModule() + " - " + link.getKey(),
                        link.getValue()
                );
            }
        }

        downloads.addLink(
                "Dashboard JSON",
                "dashboard/reports/dashboard.json"
        );

        section.addWidget(downloads);

        return section;
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
        widget.setStatus(WidgetStatus.INFORMATION);
        widget.addData("value", value);

        return widget;
    }

    private DashboardModule findModule(
            DashboardSummary dashboard,
            String moduleName
    ) {
        return dashboard.getModules().stream()
                .filter(module -> module != null)
                .filter(module -> module.getModule() != null)
                .filter(module -> module.getModule()
                        .equalsIgnoreCase(moduleName))
                .findFirst()
                .orElse(null);
    }

    private WidgetStatus mapStatus(
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
