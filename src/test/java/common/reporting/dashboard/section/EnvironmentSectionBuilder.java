package common.reporting.dashboard.section;

import common.reporting.dashboard.DashboardSummary;
import common.reporting.dashboard.widget.DashboardSection;
import common.reporting.dashboard.widget.DashboardWidgetFactory;
import common.reporting.dashboard.widget.WidgetType;
import common.reporting.model.ExecutionEnvironment;

public class EnvironmentSectionBuilder
                extends AbstractDashboardSectionBuilder {

        private final DashboardWidgetFactory widgetFactory;

        public EnvironmentSectionBuilder() {
                this(new DashboardWidgetFactory());
        }

        EnvironmentSectionBuilder(
                        DashboardWidgetFactory widgetFactory) {
                if (widgetFactory == null) {
                        throw new IllegalArgumentException(
                                        "Dashboard widget factory must not be null");
                }

                this.widgetFactory = widgetFactory;
        }

        @Override
        public String getSectionId() {
                return "environment";
        }

        @Override
        public int getDisplayOrder() {
                return 4;
        }

        @Override
        public DashboardSection build(
                        DashboardSummary summary) {
                if (summary == null) {
                        throw new IllegalArgumentException(
                                        "Dashboard summary must not be null");
                }

                DashboardSection section = new DashboardSection(
                                getSectionId(),
                                "Environment",
                                getDisplayOrder());

                ExecutionEnvironment environment = summary.getEnvironment();

                if (environment == null) {
                        section.addWidget(
                                        widgetFactory.createNotAvailableWidget(
                                                        "execution-environment",
                                                        "Execution Environment",
                                                        WidgetType.ENVIRONMENT,
                                                        1));

                        return section;
                }

                section.addWidget(
                                widgetFactory.createEnvironmentWidget(
                                                "execution-environment",
                                                "Execution Environment",
                                                environment.getEnvironment(),
                                                environment.getBuild(),
                                                environment.getBranch(),
                                                environment.getCommit(),
                                                environment.getExecutionId(),
                                                1));

                return section;
        }
}
