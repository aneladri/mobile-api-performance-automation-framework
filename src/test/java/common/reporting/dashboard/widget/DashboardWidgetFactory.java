package common.reporting.dashboard.widget;

import common.reporting.model.ExecutionStatus;

public class DashboardWidgetFactory {

        public DashboardWidget createStatusWidget(
                        String id,
                        String title,
                        ExecutionStatus executionStatus,
                        int displayOrder) {
                validateRequiredFields(id, title);

                if (executionStatus == null) {
                        throw new IllegalArgumentException(
                                        "Execution status must not be null");
                }

                DashboardWidget widget = new DashboardWidget(
                                id,
                                title,
                                WidgetType.STATUS);

                widget.setDisplayOrder(displayOrder);
                widget.setStatus(mapStatus(executionStatus));
                widget.addData("value", executionStatus.name());

                return widget;
        }

        public DashboardWidget createKpiWidget(
                        String id,
                        String title,
                        Object value,
                        int displayOrder) {
                validateRequiredFields(id, title);

                DashboardWidget widget = new DashboardWidget(
                                id,
                                title,
                                WidgetType.KPI);

                widget.setDisplayOrder(displayOrder);
                widget.setStatus(WidgetStatus.INFORMATION);
                widget.addData("value", value);

                return widget;
        }

        public DashboardWidget createKpiWidget(
                        String id,
                        String title,
                        Object value,
                        String unit,
                        int displayOrder) {
                DashboardWidget widget = createKpiWidget(
                                id,
                                title,
                                value,
                                displayOrder);

                if (unit != null && !unit.isBlank()) {
                        widget.addData("unit", unit);
                }

                return widget;
        }

        public DashboardWidget createKpiWidget(
                        String id,
                        String title,
                        Object value,
                        WidgetStatus status,
                        int displayOrder) {
                DashboardWidget widget = createKpiWidget(
                                id,
                                title,
                                value,
                                displayOrder);

                if (status != null) {
                        widget.setStatus(status);
                }

                return widget;
        }

        public DashboardWidget createModuleWidget(
                        String id,
                        String title,
                        ExecutionStatus executionStatus,
                        int total,
                        int passed,
                        int failed,
                        int skipped,
                        int displayOrder) {
                validateRequiredFields(id, title);

                DashboardWidget widget = new DashboardWidget(
                                id,
                                title,
                                WidgetType.MODULE);

                widget.setDisplayOrder(displayOrder);
                widget.setStatus(mapStatus(executionStatus));

                widget.addData("status", resolveStatusValue(executionStatus));
                widget.addData("total", total);
                widget.addData("passed", passed);
                widget.addData("failed", failed);
                widget.addData("skipped", skipped);

                return widget;
        }

        public DashboardWidget createEnvironmentWidget(
                        String id,
                        String title,
                        String environment,
                        String buildNumber,
                        String branch,
                        String executionType,
                        String executionId,
                        int displayOrder) {
                validateRequiredFields(id, title);

                DashboardWidget widget = new DashboardWidget(
                                id,
                                title,
                                WidgetType.ENVIRONMENT);

                widget.setDisplayOrder(displayOrder);
                widget.setStatus(WidgetStatus.INFORMATION);

                widget.addData(
                                "environment",
                                defaultValue(environment));
                widget.addData(
                                "buildNumber",
                                defaultValue(buildNumber));
                widget.addData(
                                "branch",
                                defaultValue(branch));
                widget.addData(
                                "executionType",
                                defaultValue(executionType));
                widget.addData(
                                "executionId",
                                defaultValue(executionId));

                return widget;
        }

        public DashboardWidget createDownloadWidget(
                        String id,
                        String title,
                        String label,
                        String path,
                        int displayOrder) {
                validateRequiredFields(id, title);

                DashboardWidget widget = new DashboardWidget(
                                id,
                                title,
                                WidgetType.DOWNLOAD);

                widget.setDisplayOrder(displayOrder);

                if (path == null || path.isBlank()) {
                        widget.setStatus(WidgetStatus.NOT_AVAILABLE);
                        widget.addData("label", defaultValue(label));
                        widget.addData("available", false);

                        return widget;
                }

                widget.setStatus(WidgetStatus.INFORMATION);
                widget.addData("label", defaultValue(label));
                widget.addData("path", path);
                widget.addData("available", true);

                return widget;
        }

        public DashboardWidget createNotAvailableWidget(
                        String id,
                        String title,
                        WidgetType widgetType,
                        int displayOrder) {
                validateRequiredFields(id, title);

                if (widgetType == null) {
                        throw new IllegalArgumentException(
                                        "Widget type must not be null");
                }

                DashboardWidget widget = new DashboardWidget(
                                id,
                                title,
                                widgetType);

                widget.setDisplayOrder(displayOrder);
                widget.setStatus(WidgetStatus.NOT_AVAILABLE);
                widget.addData("available", false);
                widget.addData("message", "Data not available");

                return widget;
        }

        private WidgetStatus mapStatus(
                        ExecutionStatus executionStatus) {
                if (executionStatus == null) {
                        return WidgetStatus.NOT_AVAILABLE;
                }

                return switch (executionStatus) {
                        case PASS -> WidgetStatus.SUCCESS;
                        case FAIL -> WidgetStatus.FAILURE;
                        case PARTIAL -> WidgetStatus.WARNING;
                        case NOT_RUN -> WidgetStatus.NOT_AVAILABLE;
                        case SKIPPED -> WidgetStatus.WARNING;
                };
        }

        private String resolveStatusValue(
                        ExecutionStatus executionStatus) {
                return executionStatus == null
                                ? ExecutionStatus.NOT_RUN.name()
                                : executionStatus.name();
        }

        private String defaultValue(String value) {
                return value == null || value.isBlank()
                                ? "N/A"
                                : value;
        }

        private void validateRequiredFields(
                        String id,
                        String title) {
                if (id == null || id.isBlank()) {
                        throw new IllegalArgumentException(
                                        "Widget id must not be blank");
                }

                if (title == null || title.isBlank()) {
                        throw new IllegalArgumentException(
                                        "Widget title must not be blank");
                }
        }
}
