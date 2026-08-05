package dashboard.enterprise.doctor.probe.dashboard;

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

public final class DashboardHealthProbe implements HealthProbe {

    private final DashboardInspector inspector;

    private final HealthProbeDefinition definition =
            new HealthProbeDefinition(
                    "dashboard-health",
                    "Dashboard Health",
                    "1.0",
                    HealthSeverity.HIGH,
                    true,
                    false,
                    "Validates MAPAF executive dashboards, "
                            + "report contracts, internal links, "
                            + "evidence pages, and optional HTTP serving.",
                    List.of(
                            "repository-foundation",
                            "environment-health"
                    )
            );

    public DashboardHealthProbe() {
        this(new FileSystemDashboardInspector());
    }

    public DashboardHealthProbe(
            DashboardInspector inspector
    ) {
        if (inspector == null) {
            throw new IllegalArgumentException(
                    "Dashboard inspector is required."
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
        DashboardInspectionResult inspection =
                inspector.inspect(context.repositoryRoot());

        List<DiagnosticCheck> checks = new ArrayList<>();

        addReportCheck(
                checks,
                inspection,
                "command-center",
                "Command Center",
                true
        );

        addReportCheck(
                checks,
                inspection,
                "executive-summary",
                "Executive Summary",
                true
        );

        addReportCheck(
                checks,
                inspection,
                "release-readiness-html",
                "Release Readiness HTML",
                true
        );

        addReportCheck(
                checks,
                inspection,
                "release-readiness-json",
                "Release Readiness Contract",
                true
        );

        addReportCheck(
                checks,
                inspection,
                "doctor-html",
                "MAPAF Doctor HTML",
                true
        );

        addReportCheck(
                checks,
                inspection,
                "doctor-json",
                "MAPAF Doctor Contract",
                true
        );

        addReportCheck(
                checks,
                inspection,
                "allure",
                "Allure Evidence Hub",
                false
        );

        addReportCheck(
                checks,
                inspection,
                "mobile",
                "Mobile Dashboard",
                false
        );

        addReportCheck(
                checks,
                inspection,
                "web",
                "Web Dashboard",
                false
        );

        addReportCheck(
                checks,
                inspection,
                "api",
                "API Dashboard",
                false
        );

        addReportCheck(
                checks,
                inspection,
                "performance",
                "Performance Dashboard",
                false
        );

        checks.add(contractCheck(
                "release-readiness-schema",
                "Release Readiness Schema",
                inspection.contractVersions().getOrDefault(
                        "releaseReadiness",
                        "MISSING"
                ),
                "mapaf.release-readiness/v3",
                true
        ));

        checks.add(contractCheck(
                "executive-decision-schema",
                "Executive Decision Schema",
                inspection.contractVersions().getOrDefault(
                        "executiveDecision",
                        "MISSING"
                ),
                "mapaf.executive-decision/v1",
                true
        ));

        checks.add(contractCheck(
                "doctor-schema",
                "MAPAF Doctor Schema",
                inspection.contractVersions().getOrDefault(
                        "doctor",
                        "MISSING"
                ),
                "mapaf.doctor/v1",
                true
        ));

        checks.add(linkCheck(inspection));
        checks.add(httpCheck(inspection));

        int criticalFailures = 0;
        int warnings = 0;
        int passed = 0;

        for (DiagnosticCheck check : checks) {
            if (check.status() == DiagnosticStatus.PASS
                    || check.status()
                    == DiagnosticStatus.SKIPPED) {
                passed++;
                continue;
            }

            boolean critical = Boolean.TRUE.equals(
                    check.measurements().get("critical")
            );

            if (critical) {
                criticalFailures++;
            } else {
                warnings++;
            }
        }

        HealthStatus status;

        if (criticalFailures > 0) {
            status = HealthStatus.UNHEALTHY;
        } else if (warnings > 0) {
            status = HealthStatus.DEGRADED;
        } else {
            status = HealthStatus.HEALTHY;
        }

        List<String> actions = checks.stream()
                .filter(check ->
                        check.status() == DiagnosticStatus.FAIL
                                || check.status()
                                == DiagnosticStatus.WARN)
                .map(DiagnosticCheck::correctiveAction)
                .filter(value -> !value.isBlank())
                .distinct()
                .toList();

        List<String> evidence = inspection.reportAvailability()
                .entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .toList();

        Map<String, Object> metadata =
                new LinkedHashMap<>();

        metadata.put("checksTotal", checks.size());
        metadata.put("checksPassed", passed);
        metadata.put(
                "criticalFailures",
                criticalFailures
        );
        metadata.put("warnings", warnings);
        metadata.put(
                "brokenLinks",
                inspection.brokenLinks().size()
        );
        metadata.put(
                "httpCheckEnabled",
                inspection.httpCheckEnabled()
        );
        metadata.put(
                "httpReachable",
                inspection.httpReachable()
        );
        metadata.put(
                "contractVersions",
                inspection.contractVersions()
        );

        return new HealthProbeResult(
                definition.id(),
                definition.name(),
                definition.version(),
                definition.severity(),
                status,
                inspection.durationMillis(),
                summary(status, passed, checks.size()),
                diagnosis(
                        status,
                        criticalFailures,
                        warnings,
                        inspection.brokenLinks().size()
                ),
                actions,
                checks,
                evidence,
                metadata
        );
    }

    private void addReportCheck(
            List<DiagnosticCheck> checks,
            DashboardInspectionResult inspection,
            String key,
            String name,
            boolean critical
    ) {
        boolean available = inspection.reportAvailability()
                .getOrDefault(key, false);

        checks.add(
                new DiagnosticCheck(
                        "dashboard-" + key,
                        name,
                        available
                                ? DiagnosticStatus.PASS
                                : critical
                                ? DiagnosticStatus.FAIL
                                : DiagnosticStatus.WARN,
                        "Report artifact is available",
                        available ? "Available" : "Missing",
                        available
                                ? "Dashboard artifact is available."
                                : "Required dashboard artifact "
                                + "was not published.",
                        available
                                ? "No corrective action required."
                                : "Generate or restore the "
                                + name
                                + " report.",
                        Map.of("critical", critical)
                )
        );
    }

    private DiagnosticCheck contractCheck(
            String id,
            String name,
            String actual,
            String expected,
            boolean critical
    ) {
        boolean valid = expected.equals(actual);

        return new DiagnosticCheck(
                id,
                name,
                valid
                        ? DiagnosticStatus.PASS
                        : critical
                        ? DiagnosticStatus.FAIL
                        : DiagnosticStatus.WARN,
                expected,
                actual,
                valid
                        ? "Dashboard contract version is valid."
                        : "Dashboard contract version is missing, "
                        + "invalid, or incompatible.",
                valid
                        ? "No corrective action required."
                        : "Regenerate the report using the "
                        + "supported MAPAF contract.",
                Map.of("critical", critical)
        );
    }

    private DiagnosticCheck linkCheck(
            DashboardInspectionResult inspection
    ) {
        boolean healthy = inspection.linksHealthy();

        String actual = healthy
                ? "All internal links resolved"
                : inspection.brokenLinks().size()
                + " broken link(s)";

        return new DiagnosticCheck(
                "dashboard-internal-links",
                "Internal Dashboard Links",
                healthy
                        ? DiagnosticStatus.PASS
                        : DiagnosticStatus.WARN,
                "All relative dashboard links resolve",
                actual,
                healthy
                        ? "Dashboard navigation is internally consistent."
                        : String.join(
                                "; ",
                                inspection.brokenLinks()
                        ),
                healthy
                        ? "No corrective action required."
                        : "Correct or regenerate broken dashboard links.",
                Map.of(
                        "critical",
                        false,
                        "brokenLinks",
                        inspection.brokenLinks()
                )
        );
    }

    private DiagnosticCheck httpCheck(
            DashboardInspectionResult inspection
    ) {
        if (!inspection.httpCheckEnabled()) {
            return new DiagnosticCheck(
                    "dashboard-http-serving",
                    "Dashboard HTTP Serving",
                    DiagnosticStatus.SKIPPED,
                    "Optional HTTP reachability check",
                    "Disabled",
                    "HTTP validation was not enabled.",
                    "Set "
                            + "-Dmapaf.doctor.dashboard.http.enabled=true "
                            + "to enable live HTTP validation.",
                    Map.of("critical", false)
            );
        }

        return new DiagnosticCheck(
                "dashboard-http-serving",
                "Dashboard HTTP Serving",
                inspection.httpReachable()
                        ? DiagnosticStatus.PASS
                        : DiagnosticStatus.WARN,
                "Dashboard endpoint is reachable",
                inspection.httpReachable()
                        ? inspection.httpEndpoint()
                        : "Unreachable: "
                        + inspection.httpEndpoint(),
                inspection.httpReachable()
                        ? "Dashboard is reachable over HTTP."
                        : "Dashboard HTTP endpoint is unavailable.",
                inspection.httpReachable()
                        ? "No corrective action required."
                        : "Start the MAPAF report server or "
                        + "configure the correct dashboard URL.",
                Map.of("critical", false)
        );
    }

    private String summary(
            HealthStatus status,
            int passed,
            int total
    ) {
        return switch (status) {
            case HEALTHY ->
                    "MAPAF dashboard and reporting layer "
                            + "is fully operational.";
            case DEGRADED ->
                    "MAPAF dashboards are operational with "
                            + "non-critical reporting issues.";
            case UNHEALTHY ->
                    "A required MAPAF dashboard or contract "
                            + "is unavailable.";
            case NOT_CONFIGURED ->
                    "MAPAF dashboard health is not configured.";
        } + " " + passed + "/" + total + " checks passed.";
    }

    private String diagnosis(
            HealthStatus status,
            int criticalFailures,
            int warnings,
            int brokenLinks
    ) {
        return switch (status) {
            case HEALTHY ->
                    "No dashboard health issues detected.";
            case DEGRADED ->
                    warnings
                            + " non-critical dashboard issue(s) "
                            + "detected; broken links="
                            + brokenLinks
                            + ".";
            case UNHEALTHY ->
                    criticalFailures
                            + " critical dashboard or contract "
                            + "failure(s) detected.";
            case NOT_CONFIGURED ->
                    "Dashboard diagnostics were not configured.";
        };
    }
}
