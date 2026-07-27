package common.reporting.tests;

import common.reporting.aggregation.DashboardAggregator;
import common.reporting.dashboard.DashboardSummary;
import common.reporting.dashboard.builder.DashboardBuilder;
import common.reporting.dashboard.builder.DashboardPage;
import common.reporting.dashboard.config.DashboardConfiguration;
import common.reporting.dashboard.config.DashboardTab;
import common.reporting.dashboard.section.AbstractDashboardSectionBuilder;
import common.reporting.dashboard.widget.DashboardSection;
import common.reporting.model.ExecutionEnvironment;
import common.reporting.model.ExecutionMetrics;
import common.reporting.model.ExecutionStatus;
import common.reporting.model.ExecutionSummary;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class DashboardBuilderTest {

        @Test
        public void shouldBuildRegisteredDashboardSections() {
                DashboardPage page = new DashboardBuilder().build(
                                createDashboardSummary(),
                                DashboardConfiguration.defaultConfiguration());

                Assert.assertEquals(
                                page.getVisibleSections().size(),
                                6);

                Assert.assertEquals(
                                page.getVisibleSections().get(0).getId(),
                                "overview");

                Assert.assertEquals(
                                page.getVisibleSections().get(1).getId(),
                                "api");

                Assert.assertEquals(
                                page.getVisibleSections().get(2).getId(),
                                "web");

                Assert.assertEquals(
                                page.getVisibleSections().get(3).getId(),
                                "performance");

                Assert.assertEquals(
                                page.getVisibleSections().get(4).getId(),
                                "environment");

                Assert.assertEquals(
                                page.getVisibleSections().get(5).getId(),
                                "downloads");
        }

        @Test
        public void shouldRespectDisabledOverviewTab() {
                DashboardConfiguration configuration = DashboardConfiguration.defaultConfiguration();

                configuration.setEnabledTabs(
                                List.of(DashboardTab.API));

                DashboardPage page = new DashboardBuilder().build(
                                createDashboardSummary(),
                                configuration);

                Assert.assertEquals(
                                page.getVisibleSections().size(),
                                1);

                Assert.assertEquals(
                                page.getVisibleSections().get(0).getId(),
                                "api");
        }

        @Test
        public void shouldAllowAdditionalSectionBuilders() {
                DashboardBuilder builder = new DashboardBuilder();

                builder.register(
                                new TestSectionBuilder(
                                                "custom",
                                                "Custom",
                                                2));

                DashboardPage page = builder.build(
                                createDashboardSummary(),
                                DashboardConfiguration.defaultConfiguration());

                Assert.assertEquals(
                                page.getVisibleSections().size(),
                                7);

                Assert.assertEquals(
                                page.getVisibleSections().get(0).getId(),
                                "overview");

                Assert.assertEquals(
                                page.getVisibleSections().get(1).getId(),
                                "api");

                Assert.assertEquals(
                                page.getVisibleSections().get(2).getId(),
                                "custom");

                Assert.assertEquals(
                                page.getVisibleSections().get(3).getId(),
                                "web");

                Assert.assertEquals(
                                page.getVisibleSections().get(4).getId(),
                                "performance");

                Assert.assertEquals(
                                page.getVisibleSections().get(5).getId(),
                                "environment");

                Assert.assertEquals(
                                page.getVisibleSections().get(6).getId(),
                                "downloads");
        }

        @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Dashboard section builder already registered: overview")
        public void shouldRejectDuplicateSectionBuilder() {
                DashboardBuilder builder = new DashboardBuilder();

                builder.register(
                                new TestSectionBuilder(
                                                "overview",
                                                "Duplicate Overview",
                                                2));
        }

        @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Dashboard summary must not be null")
        public void shouldRejectNullDashboardSummary() {
                new DashboardBuilder().build(
                                null,
                                DashboardConfiguration.defaultConfiguration());
        }

        @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Dashboard configuration must not be null")
        public void shouldRejectNullDashboardConfiguration() {
                new DashboardBuilder().build(
                                createDashboardSummary(),
                                null);
        }

        private DashboardSummary createDashboardSummary() {
                ExecutionSummary api = new ExecutionSummary(
                                "API",
                                ExecutionStatus.PASS,
                                ExecutionMetrics.fromCounts(
                                                9,
                                                9,
                                                0,
                                                0,
                                                5),
                                new ExecutionEnvironment(
                                                "QA",
                                                "100",
                                                "sprint-2-unified-dashboard",
                                                "local",
                                                "api-001"));

                ExecutionSummary performance = new ExecutionSummary(
                                "Performance",
                                ExecutionStatus.PASS,
                                ExecutionMetrics.fromCounts(
                                                160,
                                                160,
                                                0,
                                                0,
                                                20),
                                new ExecutionEnvironment(
                                                "QA",
                                                "100",
                                                "sprint-2-unified-dashboard",
                                                "local",
                                                "performance-001"));

                return new DashboardAggregator().aggregate(
                                List.of(api, performance));
        }

        private static final class TestSectionBuilder
                        extends AbstractDashboardSectionBuilder {

                private final String sectionId;
                private final String title;
                private final int displayOrder;

                private TestSectionBuilder(
                                String sectionId,
                                String title,
                                int displayOrder) {
                        this.sectionId = sectionId;
                        this.title = title;
                        this.displayOrder = displayOrder;
                }

                @Override
                public String getSectionId() {
                        return sectionId;
                }

                @Override
                public int getDisplayOrder() {
                        return displayOrder;
                }

                @Override
                public DashboardSection build(
                                DashboardSummary summary) {
                        return new DashboardSection(
                                        sectionId,
                                        title,
                                        displayOrder);
                }
        }
}