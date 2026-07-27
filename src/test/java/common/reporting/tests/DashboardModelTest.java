package common.reporting.tests;

import common.reporting.dashboard.DashboardMetrics;
import common.reporting.dashboard.DashboardModule;
import common.reporting.dashboard.DashboardSummary;
import common.reporting.model.ExecutionEnvironment;
import common.reporting.model.ExecutionMetrics;
import common.reporting.model.ExecutionStatus;
import common.reporting.model.ExecutionSummary;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DashboardModelTest {

    @Test
    public void shouldCreateDashboardModuleFromExecutionSummary() {
        ExecutionSummary summary = new ExecutionSummary(
                "API",
                ExecutionStatus.PASS,
                ExecutionMetrics.fromCounts(10, 10, 0, 0, 12),
                new ExecutionEnvironment(
                        "QA",
                        "100",
                        "sprint-1-reporting-contract",
                        "local",
                        "api-001"
                )
        );

        DashboardModule module = DashboardModule.fromSummary(summary);

        Assert.assertEquals(module.getModule(), "API");
        Assert.assertEquals(module.getStatus(), ExecutionStatus.PASS);
        Assert.assertSame(module.getSummary(), summary);
    }

    @Test
    public void shouldAggregateDashboardMetricCounts() {
        DashboardMetrics metrics = new DashboardMetrics();

        metrics.add(10, 9, 1, 0, 15);
        metrics.add(20, 18, 1, 1, 30);

        Assert.assertEquals(metrics.getTotalTests(), 30);
        Assert.assertEquals(metrics.getPassed(), 27);
        Assert.assertEquals(metrics.getFailed(), 2);
        Assert.assertEquals(metrics.getSkipped(), 1);
        Assert.assertEquals(metrics.getDurationSeconds(), 45L);
        Assert.assertEquals(metrics.getPassRate(), 90.0);
    }

    @Test
    public void shouldAddModuleToDashboardSummary() {
        DashboardSummary dashboard = new DashboardSummary();

        DashboardModule module = new DashboardModule(
                "Performance",
                ExecutionStatus.PASS,
                null
        );

        dashboard.addModule(module);

        Assert.assertEquals(dashboard.getModules().size(), 1);
        Assert.assertEquals(
                dashboard.getModules().get(0).getModule(),
                "Performance"
        );
        Assert.assertEquals(
                dashboard.getOverallStatus(),
                ExecutionStatus.NOT_RUN
        );
    }
}
