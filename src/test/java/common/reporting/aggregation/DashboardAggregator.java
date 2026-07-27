package common.reporting.aggregation;

import common.reporting.dashboard.DashboardMetrics;
import common.reporting.dashboard.DashboardModule;
import common.reporting.dashboard.DashboardSummary;
import common.reporting.model.ExecutionEnvironment;
import common.reporting.model.ExecutionMetrics;
import common.reporting.model.ExecutionStatus;
import common.reporting.model.ExecutionSummary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DashboardAggregator {

    public DashboardSummary aggregate(List<ExecutionSummary> summaries) {
        List<ExecutionSummary> safeSummaries =
                summaries == null ? Collections.emptyList() : summaries;

        DashboardMetrics dashboardMetrics = new DashboardMetrics();
        List<DashboardModule> modules = new ArrayList<>();

        ExecutionEnvironment dashboardEnvironment = null;
        ExecutionStatus overallStatus = ExecutionStatus.NOT_RUN;

        for (ExecutionSummary summary : safeSummaries) {
            if (summary == null) {
                continue;
            }

            validate(summary);

            modules.add(DashboardModule.fromSummary(summary));

            ExecutionMetrics metrics = summary.getMetrics();

            dashboardMetrics.add(
                    metrics.getTotal(),
                    metrics.getPassed(),
                    metrics.getFailed(),
                    metrics.getSkipped(),
                    metrics.getDurationSeconds()
            );

            if (dashboardEnvironment == null) {
                dashboardEnvironment = summary.getEnvironment();
            }

            overallStatus = combineStatus(
                    overallStatus,
                    summary.getStatus()
            );
        }

        return new DashboardSummary(
                overallStatus,
                dashboardMetrics,
                dashboardEnvironment,
                modules
        );
    }

    private void validate(ExecutionSummary summary) {
        Objects.requireNonNull(
                summary.getModule(),
                "Execution summary module must not be null"
        );

        if (summary.getModule().isBlank()) {
            throw new IllegalArgumentException(
                    "Execution summary module must not be blank"
            );
        }

        Objects.requireNonNull(
                summary.getStatus(),
                "Execution summary status must not be null"
        );

        Objects.requireNonNull(
                summary.getMetrics(),
                "Execution summary metrics must not be null"
        );

        Objects.requireNonNull(
                summary.getEnvironment(),
                "Execution summary environment must not be null"
        );
    }

    private ExecutionStatus combineStatus(
            ExecutionStatus current,
            ExecutionStatus next
    ) {
        if (next == ExecutionStatus.FAIL) {
            return ExecutionStatus.FAIL;
        }

        if (current == ExecutionStatus.FAIL) {
            return ExecutionStatus.FAIL;
        }

        if (next == ExecutionStatus.PARTIAL) {
            return ExecutionStatus.PARTIAL;
        }

        if (current == ExecutionStatus.PARTIAL) {
            return ExecutionStatus.PARTIAL;
        }

        if (next == ExecutionStatus.PASS) {
            if (current == ExecutionStatus.NOT_RUN
                    || current == ExecutionStatus.SKIPPED) {
                return ExecutionStatus.PASS;
            }

            return current;
        }

        if (next == ExecutionStatus.SKIPPED) {
            if (current == ExecutionStatus.NOT_RUN) {
                return ExecutionStatus.SKIPPED;
            }

            return current;
        }

        return current;
    }
}
