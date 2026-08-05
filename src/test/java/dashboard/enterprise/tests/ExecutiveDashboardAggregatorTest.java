package dashboard.enterprise.tests;

import dashboard.enterprise.aggregation.ExecutiveDashboardAggregator;
import dashboard.enterprise.model.EnterpriseModuleView;
import dashboard.enterprise.model.ExecutiveDashboardView;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class ExecutiveDashboardAggregatorTest {

    @Test
    public void shouldRecommendProductionWhenAllModulesPass() {
        List<EnterpriseModuleView> modules = List.of(
                passedModule("mobile"),
                passedModule("web"),
                passedModule("api"),
                passedModule("performance")
        );

        ExecutiveDashboardView view = new ExecutiveDashboardAggregator().aggregate(modules);

        Assert.assertEquals(view.overallStatus(), "PASS");
        Assert.assertEquals(view.risk(), "LOW");
        Assert.assertEquals(view.recommendation(), "READY FOR PRODUCTION");
        Assert.assertEquals(view.readinessScore(), 100);
    }

    @Test
    public void shouldRemainPartialWhenRequiredModuleHasNotRun() {
        List<EnterpriseModuleView> modules = List.of(
                passedModule("mobile"),
                passedModule("web"),
                passedModule("api"),
                notRunModule("performance")
        );

        ExecutiveDashboardView view = new ExecutiveDashboardAggregator().aggregate(modules);

        Assert.assertEquals(view.overallStatus(), "PARTIAL");
        Assert.assertEquals(view.risk(), "MEDIUM");
        Assert.assertEquals(
                view.recommendation(),
                "COMPLETE REMAINING QUALITY VALIDATIONS"
        );
        Assert.assertEquals(view.readinessScore(), 75);
    }

    private EnterpriseModuleView passedModule(String key) {
        return new EnterpriseModuleView(
                key,
                key,
                "PASS",
                "scenario",
                "QA",
                100.0,
                1000,
                1,
                1,
                0,
                Map.of(),
                Map.of()
        );
    }

    private EnterpriseModuleView notRunModule(String key) {
        return new EnterpriseModuleView(
                key,
                key,
                "NOT_RUN",
                "scenario",
                "QA",
                0.0,
                0,
                0,
                0,
                0,
                Map.of(),
                Map.of()
        );
    }
}
