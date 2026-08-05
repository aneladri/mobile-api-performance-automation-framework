package dashboard.enterprise.doctor.probe.performance;

import dashboard.enterprise.doctor.model.DiagnosticCheck;
import dashboard.enterprise.doctor.model.DiagnosticStatus;
import dashboard.enterprise.doctor.model.HealthProbeDefinition;
import dashboard.enterprise.doctor.model.HealthProbeResult;
import dashboard.enterprise.doctor.model.HealthSeverity;
import dashboard.enterprise.doctor.model.HealthStatus;
import dashboard.enterprise.doctor.probe.DoctorContext;
import dashboard.enterprise.doctor.probe.HealthProbe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PerformanceHealthProbe
        implements HealthProbe {

    private final PerformanceInspector inspector;

    private final HealthProbeDefinition definition =
            new HealthProbeDefinition(
                    "performance-health",
                    "Performance Health",
                    "1.0",
                    HealthSeverity.HIGH,
                    true,
                    false,
                    "Validates performance tooling, workload evidence, "
                            + "request volume, latency, error rate, and "
                            + "threshold compliance.",
                    List.of(
                            "environment-health",
                            "dashboard-health"
                    )
            );

    public PerformanceHealthProbe() {
        this(new FileSystemPerformanceInspector());
    }

    public PerformanceHealthProbe(
            PerformanceInspector inspector
    ) {
        if (inspector == null) {
            throw new IllegalArgumentException(
                    "Performance inspector is required."
            );
        }

        this.inspector = inspector;
    }

    @Override
    public HealthProbeDefinition definition() {
        return definition;
    }

    @Override
    public HealthProbeResult execute(
            DoctorContext context
    ) {
        PerformanceInspectionResult inspection =
                inspector.inspect(context);

        List<DiagnosticCheck> checks =
                new ArrayList<>();

        checks.add(toolCheck(
                "performance-k6",
                "k6 Runtime",
                inspection.k6Available(),
                "k6 is installed and executable"
        ));

        checks.add(toolCheck(
                "performance-jmeter",
                "JMeter Runtime",
                inspection.jmeterAvailable(),
                "JMeter is installed and executable"
        ));

        checks.add(check(
                "performance-enterprise-summary",
                "Performance Enterprise Summary",
                inspection.enterpriseSummaryAvailable(),
                "Performance enterprise summary is available",
                inspection.enterpriseSummaryAvailable()
                        ? "Available"
                        : "Missing",
                true,
                "Run performanceEnterpriseDemo to publish "
                        + "the performance summary."
        ));

        checks.add(check(
                "performance-dashboard",
                "Performance Dashboard",
                inspection.dashboardAvailable(),
                "Performance dashboard is available",
                inspection.dashboardAvailable()
                        ? "Available"
                        : "Missing",
                false,
                "Run frameworkDashboardDemo to publish "
                        + "the performance dashboard."
        ));

        checks.add(check(
                "performance-failure-showcase",
                "Performance Failure Showcase",
                inspection.failureShowcaseAvailable(),
                "Performance failure showcase is available",
                inspection.failureShowcaseAvailable()
                        ? "Available"
                        : "Missing",
                false,
                "Generate the performance failure showcase."
        ));

        checks.add(check(
                "performance-workload-status",
                "Performance Workload Status",
                inspection.workloadPassed(),
                "Published workload status is PASS",
                inspection.workloadPassed()
                        ? "PASS"
                        : "NOT PASSING",
                true,
                "Re-run the performance workload and investigate "
                        + "the failed threshold or transaction."
        ));

        checks.add(metricCheck(
                "performance-request-volume",
                "Performance Request Volume",
                inspection.totalRequests() > 0,
                "Request count is greater than zero",
                inspection.totalRequests() + " requests",
                false,
                "Run a valid performance workload with measurable traffic."
        ));

        checks.add(metricCheck(
                "performance-error-rate",
                "Performance Error Rate",
                inspection.errorRateHealthy(),
                "Error rate <= "
                        + inspection.maxAllowedErrorRatePercent()
                        + "%",
                inspection.errorRatePercent() + "%",
                true,
                "Investigate failed transactions and reduce "
                        + "the workload error rate."
        ));

        checks.add(metricCheck(
                "performance-p95-latency",
                "Performance P95 Latency",
                inspection.latencyHealthy(),
                "P95 latency <= "
                        + inspection.maxAllowedP95LatencyMillis()
                        + " ms",
                inspection.p95LatencyMillis() + " ms",
                true,
                "Investigate latency bottlenecks and restore "
                        + "the configured P95 SLA."
        ));

        checks.add(check(
                "performance-production-workload",
                "Production Workload Evidence",
                inspection.productionWorkloadEvidenceAvailable(),
                "Production-scale workload evidence is available",
                inspection.productionWorkloadEvidenceAvailable()
                        ? "Available"
                        : "Not available",
                false,
                "Run performanceProductionDemo to publish "
                        + "production-scale evidence."
        ));

        HealthStatus status =
                determineStatus(inspection);

        int passed = (int) checks.stream()
                .filter(check ->
                        check.status() == DiagnosticStatus.PASS
                                || check.status()
                                == DiagnosticStatus.SKIPPED)
                .count();

        List<String> actions = checks.stream()
                .filter(check ->
                        check.status() == DiagnosticStatus.FAIL
                                || check.status()
                                == DiagnosticStatus.WARN)
                .map(DiagnosticCheck::correctiveAction)
                .filter(value -> !value.isBlank())
                .distinct()
                .toList();

        Map<String, Object> metadata =
                new LinkedHashMap<>(inspection.metadata());

        metadata.put(
                "totalRequests",
                inspection.totalRequests()
        );

        metadata.put(
                "errorRatePercent",
                inspection.errorRatePercent()
        );

        metadata.put(
                "p95LatencyMillis",
                inspection.p95LatencyMillis()
        );

        metadata.put(
                "workloadPassed",
                inspection.workloadPassed()
        );

        metadata.put(
                "k6Available",
                inspection.k6Available()
        );

        metadata.put(
                "jmeterAvailable",
                inspection.jmeterAvailable()
        );

        return new HealthProbeResult(
                definition.id(),
                definition.name(),
                definition.version(),
                definition.severity(),
                status,
                inspection.durationMillis(),
                summary(status, passed, checks.size()),
                inspection.diagnosis(),
                actions,
                checks,
                inspection.evidenceReferences(),
                metadata
        );
    }

    private DiagnosticCheck toolCheck(
            String id,
            String name,
            boolean available,
            String expected
    ) {
        return new DiagnosticCheck(
                id,
                name,
                available
                        ? DiagnosticStatus.PASS
                        : DiagnosticStatus.WARN,
                expected,
                available ? "Available" : "Unavailable",
                available
                        ? "Performance runtime is available."
                        : "Performance runtime is unavailable.",
                available
                        ? "No corrective action required."
                        : "Install " + name
                        + " to enable direct workload execution.",
                Map.of("critical", false)
        );
    }

    private DiagnosticCheck metricCheck(
            String id,
            String name,
            boolean passed,
            String expected,
            String actual,
            boolean critical,
            String action
    ) {
        return check(
                id,
                name,
                passed,
                expected,
                actual,
                critical,
                action
        );
    }

    private DiagnosticCheck check(
            String id,
            String name,
            boolean passed,
            String expected,
            String actual,
            boolean critical,
            String action
    ) {
        return new DiagnosticCheck(
                id,
                name,
                passed
                        ? DiagnosticStatus.PASS
                        : critical
                        ? DiagnosticStatus.FAIL
                        : DiagnosticStatus.WARN,
                expected,
                actual,
                passed
                        ? "Performance diagnostic passed."
                        : "Performance diagnostic did not satisfy "
                        + "the governed health expectation.",
                passed
                        ? "No corrective action required."
                        : action,
                Map.of("critical", critical)
        );
    }

    private HealthStatus determineStatus(
            PerformanceInspectionResult inspection
    ) {
        if (!inspection.enterpriseSummaryAvailable()
                || !inspection.workloadPassed()
                || !inspection.errorRateHealthy()
                || !inspection.latencyHealthy()) {
            return HealthStatus.UNHEALTHY;
        }

        if (!inspection.toolsAvailable()
                || !inspection.dashboardAvailable()
                || !inspection.failureShowcaseAvailable()
                || !inspection.productionWorkloadEvidenceAvailable()) {
            return HealthStatus.DEGRADED;
        }

        return HealthStatus.HEALTHY;
    }

    private String summary(
            HealthStatus status,
            int passed,
            int total
    ) {
        return switch (status) {
            case HEALTHY ->
                    "Performance tooling, evidence, and SLAs "
                            + "are fully operational.";
            case DEGRADED ->
                    "Performance quality is passing, but one or more "
                            + "tools or supporting evidence artifacts "
                            + "require attention.";
            case UNHEALTHY ->
                    "A critical performance workload or SLA "
                            + "requirement is failing.";
            case NOT_CONFIGURED ->
                    "Performance health is not configured.";
        } + " " + passed + "/" + total + " checks passed.";
    }
}
