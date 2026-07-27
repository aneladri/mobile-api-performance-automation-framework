package common.reporting.tests;

import common.reporting.dashboard.DashboardSummary;
import common.reporting.dashboard.section.AbstractDashboardSectionBuilder;
import common.reporting.dashboard.section.DashboardSectionRegistry;
import common.reporting.dashboard.widget.DashboardSection;
import common.reporting.dashboard.widget.WidgetStatus;
import common.reporting.model.ExecutionStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DashboardSectionFrameworkTest {

    @Test
    public void shouldRegisterAndSortSectionBuilders() {
        DashboardSectionRegistry registry =
                new DashboardSectionRegistry();

        registry.register(new TestSectionBuilder(
                "second",
                "Second",
                2
        ));

        registry.register(new TestSectionBuilder(
                "first",
                "First",
                1
        ));

        Assert.assertEquals(registry.size(), 2);
        Assert.assertEquals(
                registry.getBuilders().get(0).getSectionId(),
                "first"
        );
        Assert.assertEquals(
                registry.getBuilders().get(1).getSectionId(),
                "second"
        );
    }

    @Test
    public void shouldDetectRegisteredSection() {
        DashboardSectionRegistry registry =
                new DashboardSectionRegistry();

        registry.register(new TestSectionBuilder(
                "overview",
                "Overview",
                1
        ));

        Assert.assertTrue(registry.contains("overview"));
        Assert.assertTrue(registry.contains("OVERVIEW"));
        Assert.assertFalse(registry.contains("api"));
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Dashboard section builder already registered: overview"
    )
    public void shouldRejectDuplicateSectionBuilder() {
        DashboardSectionRegistry registry =
                new DashboardSectionRegistry();

        registry.register(new TestSectionBuilder(
                "overview",
                "Overview",
                1
        ));

        registry.register(new TestSectionBuilder(
                "overview",
                "Overview Duplicate",
                2
        ));
    }

    @Test
    public void shouldMapExecutionStatusToWidgetStatus() {
        TestSectionBuilder builder = new TestSectionBuilder(
                "overview",
                "Overview",
                1
        );

        Assert.assertEquals(
                builder.map(ExecutionStatus.PASS),
                WidgetStatus.SUCCESS
        );

        Assert.assertEquals(
                builder.map(ExecutionStatus.FAIL),
                WidgetStatus.FAILURE
        );

        Assert.assertEquals(
                builder.map(ExecutionStatus.PARTIAL),
                WidgetStatus.WARNING
        );

        Assert.assertEquals(
                builder.map(ExecutionStatus.NOT_RUN),
                WidgetStatus.NOT_AVAILABLE
        );
    }

    private static final class TestSectionBuilder
            extends AbstractDashboardSectionBuilder {

        private final String sectionId;
        private final String title;
        private final int displayOrder;

        private TestSectionBuilder(
                String sectionId,
                String title,
                int displayOrder
        ) {
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
                DashboardSummary summary
        ) {
            return new DashboardSection(
                    sectionId,
                    title,
                    displayOrder
            );
        }

        private WidgetStatus map(ExecutionStatus status) {
            return mapStatus(status);
        }
    }
}
