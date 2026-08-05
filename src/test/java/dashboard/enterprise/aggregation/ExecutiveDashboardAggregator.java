package dashboard.enterprise.aggregation;

import dashboard.enterprise.model.EnterpriseModuleView;
import dashboard.enterprise.model.ExecutiveDashboardView;

import java.time.Instant;
import java.util.List;

public final class ExecutiveDashboardAggregator {

    public ExecutiveDashboardView aggregate(List<EnterpriseModuleView> modules) {
        List<EnterpriseModuleView> safeModules = modules == null
                ? List.of()
                : List.copyOf(modules);

        long requiredModules = safeModules.size();
        long executed = safeModules.stream()
                .filter(EnterpriseModuleView::hasRun)
                .count();
        long passed = safeModules.stream()
                .filter(EnterpriseModuleView::passedExecution)
                .count();
        long failed = safeModules.stream()
                .filter(module -> "FAIL".equalsIgnoreCase(module.status()))
                .count();
        long missingModules = Math.max(0L, requiredModules - executed);

        double executionSuccessRate = executed == 0
                ? 0.0
                : passed * 100.0 / executed;
        long totalDurationMillis = safeModules.stream()
                .mapToLong(EnterpriseModuleView::durationMillis)
                .sum();

        int readinessScore = requiredModules == 0
                ? 0
                : (int) Math.round(passed * 100.0 / requiredModules);

        String overallStatus;
        if (failed > 0) {
            overallStatus = "FAIL";
        } else if (executed == 0) {
            overallStatus = "NOT_RUN";
        } else if (missingModules > 0) {
            overallStatus = "PARTIAL";
        } else {
            overallStatus = "PASS";
        }

        String risk = failed > 0
                ? "HIGH"
                : missingModules > 0
                ? "MEDIUM"
                : "LOW";

        String recommendation = failed > 0
                ? "HOLD RELEASE AND INVESTIGATE FAILED QUALITY GATES"
                : missingModules > 0
                ? "COMPLETE REMAINING QUALITY VALIDATIONS"
                : "READY FOR PRODUCTION";

        String narrative = buildNarrative(
                safeModules,
                failed,
                executed,
                missingModules,
                recommendation
        );

        return new ExecutiveDashboardView(
                "RoomScan",
                resolveEnvironment(safeModules),
                Instant.now().toString(),
                safeModules,
                readinessScore,
                risk,
                recommendation,
                overallStatus,
                executionSuccessRate,
                totalDurationMillis,
                narrative
        );
    }

    private String resolveEnvironment(List<EnterpriseModuleView> modules) {
        return modules.stream()
                .map(EnterpriseModuleView::environment)
                .filter(value -> value != null
                        && !value.isBlank()
                        && !"N/A".equalsIgnoreCase(value))
                .findFirst()
                .orElse("QA");
    }

    private String buildNarrative(
            List<EnterpriseModuleView> modules,
            long failed,
            long executed,
            long missingModules,
            String recommendation
    ) {
        if (executed == 0) {
            return "No enterprise module summaries were found. Execute the Mobile, Web, API and Performance demos before presenting release readiness.";
        }

        if (failed > 0) {
            return "RoomScan validation identified one or more failed quality gates. Review the affected module evidence before making a release decision.";
        }

        if (missingModules > 0) {
            String missing = modules.stream()
                    .filter(module -> !module.hasRun())
                    .map(EnterpriseModuleView::name)
                    .reduce((left, right) -> left + ", " + right)
                    .orElse("one or more required modules");

            return "Available RoomScan quality validations passed. Results are still pending for "
                    + missing
                    + ", so the current recommendation is provisional.";
        }

        return "RoomScan validation completed successfully across Mobile, Web, API and Performance. Critical business workflows passed, evidence was captured, and the current recommendation is "
                + recommendation
                + ".";
    }
}
