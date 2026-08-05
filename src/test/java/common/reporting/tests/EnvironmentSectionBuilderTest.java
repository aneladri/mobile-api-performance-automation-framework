package common.reporting.tests;

import common.reporting.dashboard.DashboardSummary;
import common.reporting.dashboard.section.EnvironmentSectionBuilder;
import common.reporting.dashboard.widget.DashboardSection;
import common.reporting.dashboard.widget.DashboardWidget;
import common.reporting.dashboard.widget.WidgetStatus;
import common.reporting.dashboard.widget.WidgetType;
import common.reporting.model.ExecutionEnvironment;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EnvironmentSectionBuilderTest {

        @Test
        public void shouldBuildEnvironmentSection() {
                DashboardSummary summary = createSummary();

                DashboardSection section = new EnvironmentSectionBuilder().build(summary);

                Assert.assertEquals(
                                section.getId(),
                                "environment");

                Assert.assertEquals(
                                section.getTitle(),
                                "Environment");

                Assert.assertEquals(
                                section.getDisplayOrder(),
                                4);

                Assert.assertEquals(
                                section.getVisibleWidgets().size(),
                                1);
        }

        @Test
        public void shouldPopulateEnvironmentWidget() {
                DashboardSection section = new EnvironmentSectionBuilder().build(
                                createSummary());

                DashboardWidget widget = section.getVisibleWidgets().get(0);

                Assert.assertEquals(
                                widget.getId(),
                                "execution-environment");

                Assert.assertEquals(
                                widget.getType(),
                                WidgetType.ENVIRONMENT);

                Assert.assertEquals(
                                widget.getStatus(),
                                WidgetStatus.INFORMATION);

                Assert.assertEquals(
                                widget.getData().get("environment"),
                                "QA");

                Assert.assertEquals(
                                widget.getData().get("buildNumber"),
                                "109");

                Assert.assertEquals(
                                widget.getData().get("branch"),
                                "sprint-2-unified-dashboard");

                Assert.assertEquals(
                                widget.getData().get("executionType"),
                                "local");

                Assert.assertEquals(
                                widget.getData().get("executionId"),
                                "environment-001");
        }

        @Test
        public void shouldCreateNotAvailableWidgetWhenEnvironmentMissing() {
                DashboardSummary summary = new DashboardSummary();
                summary.setEnvironment(null);

                DashboardSection section = new EnvironmentSectionBuilder().build(summary);

                Assert.assertEquals(
                                section.getVisibleWidgets().size(),
                                1);

                DashboardWidget widget = section.getVisibleWidgets().get(0);

                Assert.assertEquals(
                                widget.getType(),
                                WidgetType.ENVIRONMENT);

                Assert.assertEquals(
                                widget.getStatus(),
                                WidgetStatus.NOT_AVAILABLE);

                Assert.assertEquals(
                                widget.getData().get("available"),
                                false);
        }

        @Test
        public void shouldUseDefaultValuesForBlankEnvironmentFields() {
                DashboardSummary summary = new DashboardSummary();

                summary.setEnvironment(
                                new ExecutionEnvironment(
                                                null,
                                                " ",
                                                null,
                                                "",
                                                null));

                DashboardSection section = new EnvironmentSectionBuilder().build(summary);

                DashboardWidget widget = section.getVisibleWidgets().get(0);

                Assert.assertEquals(
                                widget.getData().get("environment"),
                                "N/A");

                Assert.assertEquals(
                                widget.getData().get("buildNumber"),
                                "N/A");

                Assert.assertEquals(
                                widget.getData().get("branch"),
                                "N/A");

                Assert.assertEquals(
                                widget.getData().get("executionType"),
                                "N/A");

                Assert.assertEquals(
                                widget.getData().get("executionId"),
                                "N/A");
        }

        @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Dashboard summary must not be null")
        public void shouldRejectNullDashboardSummary() {
                new EnvironmentSectionBuilder().build(null);
        }

        private DashboardSummary createSummary() {
                DashboardSummary summary = new DashboardSummary();

                summary.setEnvironment(
                                new ExecutionEnvironment(
                                                "QA",
                                                "109",
                                                "sprint-2-unified-dashboard",
                                                "local",
                                                "environment-001"));

                return summary;
        }
}
