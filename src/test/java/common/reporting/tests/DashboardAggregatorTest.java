package common.reporting.tests;

import common.reporting.aggregation.DashboardAggregator;
import common.reporting.dashboard.DashboardSummary;
import common.reporting.model.ExecutionEnvironment;
import common.reporting.model.ExecutionMetrics;
import common.reporting.model.ExecutionStatus;
import common.reporting.model.ExecutionSummary;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;

public class DashboardAggregatorTest {

    private final DashboardAggregator aggregator =
            new DashboardAggregator();

    @Test
    public void shouldAggregateModuleSummaries() {
        ExecutionSummary api = createSummary(
                "API",
                ExecutionStatus.PASS,
                10,
                10,
                0,
                0,
                12
        );

        ExecutionSummary performance = createSummary(
                "Performance",
                ExecutionStatus.PASS,
                20,
                19,
                0,
                1,
                30
        );

        DashboardSummary dashboard = aggregator.aggregate(
                Arrays.asList(api, performance)
        );

        Assert.assertEquals(
                dashboard.getOverallStatus(),
                ExecutionStatus.PASS
        );

        Assert.assertEquals(dashboard.getModules().size(), 2);
        Assert.assertEquals(dashboard.getMetrics().getTotalTests(), 30);
        Assert.assertEquals(dashboard.getMetrics().getPassed(), 29);
        Assert.assertEquals(dashboard.getMetrics().getFailed(), 0);
        Assert.assertEquals(dashboard.getMetrics().getSkipped(), 1);
        Assert.assertEquals(
                dashboard.getMetrics().getDurationSeconds(),
                42L
        );
        Assert.assertEquals(
                dashboard.getMetrics().getPassRate(),
                29 * 100.0 / 30
        );
    }

    @Test
    public void shouldReturnFailWhenAnyModuleFails() {
        ExecutionSummary api = createSummary(
                "API",
                ExecutionStatus.PASS,
                10,
                10,
                0,
                0,
                10
        );

        ExecutionSummary performance = createSummary(
                "Performance",
                ExecutionStatus.FAIL,
                10,
                8,
                2,
                0,
                20
        );

        DashboardSummary dashboard = aggregator.aggregate(
                Arrays.asList(api, performance)
        );

        Assert.assertEquals(
                dashboard.getOverallStatus(),
                ExecutionStatus.FAIL
        );
    }

    @Test
    public void shouldReturnPartialWhenAnyModuleIsPartialAndNoneFail() {
        ExecutionSummary api = createSummary(
                "API",
                ExecutionStatus.PASS,
                10,
                10,
                0,
                0,
                10
        );

        ExecutionSummary performance = createSummary(
                "Performance",
                ExecutionStatus.PARTIAL,
                10,
                9,
                0,
                1,
                20
        );

        DashboardSummary dashboard = aggregator.aggregate(
                Arrays.asList(api, performance)
        );

        Assert.assertEquals(
                dashboard.getOverallStatus(),
                ExecutionStatus.PARTIAL
        );
    }

    @Test
    public void shouldReturnNotRunForEmptyInput() {
        DashboardSummary dashboard = aggregator.aggregate(
                Collections.emptyList()
        );

        Assert.assertEquals(
                dashboard.getOverallStatus(),
                ExecutionStatus.NOT_RUN
        );

        Assert.assertEquals(dashboard.getModules().size(), 0);
        Assert.assertEquals(dashboard.getMetrics().getTotalTests(), 0);
    }

    @Test
    public void shouldIgnoreNullSummaries() {
        ExecutionSummary api = createSummary(
                "API",
                ExecutionStatus.PASS,
                5,
                5,
                0,
                0,
                4
        );

        DashboardSummary dashboard = aggregator.aggregate(
                Arrays.asList(null, api, null)
        );

        Assert.assertEquals(dashboard.getModules().size(), 1);
        Assert.assertEquals(
                dashboard.getOverallStatus(),
                ExecutionStatus.PASS
        );
    }

    private ExecutionSummary createSummary(
            String module,
            ExecutionStatus status,
            int total,
            int passed,
            int failed,
            int skipped,
            long durationSeconds
    ) {
        return new ExecutionSummary(
                module,
                status,
                ExecutionMetrics.fromCounts(
                        total,
                        passed,
                        failed,
                        skipped,
                        durationSeconds
                ),
                new ExecutionEnvironment(
                        "QA",
                        "100",
                        "sprint-1-reporting-contract",
                        "local",
                        module.toLowerCase() + "-001"
                )
        );
    }
}
