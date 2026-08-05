package dashboard.enterprise.doctor.probe.api;

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

public final class ApiHealthProbe implements HealthProbe {

    private final ApiInspector inspector;

    private final HealthProbeDefinition definition =
            new HealthProbeDefinition(
                    "api-health",
                    "API Health",
                    "1.0",
                    HealthSeverity.CRITICAL,
                    true,
                    false,
                    "Validates API endpoint configuration, connectivity, "
                            + "authentication, response health, latency, "
                            + "and published API automation evidence.",
                    List.of(
                            "environment-health",
                            "dashboard-health"
                    )
            );

    public ApiHealthProbe() {
        this(new HttpApiInspector());
    }

    public ApiHealthProbe(ApiInspector inspector) {
        if (inspector == null) {
            throw new IllegalArgumentException(
                    "API inspector is required."
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
        ApiInspectionResult inspection =
                inspector.inspect(context);

        List<DiagnosticCheck> checks = new ArrayList<>();

        checks.add(endpointConfigurationCheck(inspection));
        checks.add(authenticationCheck(inspection));
        checks.add(connectivityCheck(inspection));
        checks.add(statusCheck(inspection));
        checks.add(latencyCheck(inspection));
        checks.add(responseBodyCheck(inspection));

        checks.add(evidenceCheck(
                "api-enterprise-summary",
                "API Enterprise Summary",
                inspection.enterpriseSummaryAvailable(),
                true,
                "Run apiEnterpriseDemo to publish the API summary."
        ));

        checks.add(evidenceCheck(
                "api-dashboard",
                "API Dashboard",
                inspection.dashboardAvailable(),
                false,
                "Run frameworkDashboardDemo to publish the API dashboard."
        ));

        checks.add(evidenceCheck(
                "api-failure-showcase",
                "API Failure Showcase",
                inspection.failureShowcaseAvailable(),
                false,
                "Generate the API failure showcase evidence."
        ));

        HealthStatus status = determineStatus(inspection);

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

        metadata.put("endpoint", inspection.endpoint());
        metadata.put("method", inspection.method());
        metadata.put(
                "responseStatus",
                inspection.responseStatus()
        );
        metadata.put(
                "responseTimeMillis",
                inspection.responseTimeMillis()
        );
        metadata.put(
                "connectivityChecked",
                inspection.connectivityChecked()
        );
        metadata.put(
                "authenticationConfigured",
                inspection.authenticationConfigured()
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

    private DiagnosticCheck endpointConfigurationCheck(
            ApiInspectionResult inspection
    ) {
        if (!inspection.connectivityChecked()) {
            return new DiagnosticCheck(
                    "api-endpoint-configuration",
                    "API Health Endpoint",
                    DiagnosticStatus.SKIPPED,
                    "Endpoint required when live connectivity is enabled",
                    "Live connectivity disabled",
                    "API endpoint configuration was not required.",
                    "Set -Dmapaf.api.health.enabled=true and "
                            + "-Dmapaf.api.health.url=<URL> "
                            + "to enable live API validation.",
                    Map.of("critical", false)
            );
        }

        return check(
                "api-endpoint-configuration",
                "API Health Endpoint",
                inspection.endpointConfigured(),
                "API endpoint is configured",
                inspection.endpointConfigured()
                        ? inspection.endpoint()
                        : "Not configured",
                true,
                "Configure MAPAF_API_HEALTH_URL or "
                        + "-Dmapaf.api.health.url."
        );
    }

    private DiagnosticCheck authenticationCheck(
            ApiInspectionResult inspection
    ) {
        if (!inspection.authenticationRequired()) {
            return new DiagnosticCheck(
                    "api-authentication",
                    "API Authentication",
                    DiagnosticStatus.SKIPPED,
                    "Authentication required only for secured endpoints",
                    "Not required",
                    "API authentication validation is not required.",
                    "No corrective action required.",
                    Map.of("critical", false)
            );
        }

        return check(
                "api-authentication",
                "API Authentication",
                inspection.authenticationConfigured(),
                "API authentication credential is configured",
                inspection.authenticationConfigured()
                        ? "Configured"
                        : "Missing",
                true,
                "Configure MAPAF_API_TOKEN, API_TOKEN, or "
                        + "-Dmapaf.api.health.token."
        );
    }

    private DiagnosticCheck connectivityCheck(
            ApiInspectionResult inspection
    ) {
        if (!inspection.connectivityChecked()) {
            return new DiagnosticCheck(
                    "api-connectivity",
                    "API Connectivity",
                    DiagnosticStatus.SKIPPED,
                    "Optional live API connectivity check",
                    "Disabled",
                    "Live API connectivity validation was not enabled.",
                    "Set -Dmapaf.api.health.enabled=true "
                            + "to enable connectivity validation.",
                    Map.of("critical", false)
            );
        }

        return check(
                "api-connectivity",
                "API Connectivity",
                inspection.endpointReachable(),
                "Configured API endpoint is reachable",
                inspection.endpointReachable()
                        ? "Reachable"
                        : "Unreachable",
                true,
                "Start the API service or correct network access."
        );
    }

    private DiagnosticCheck statusCheck(
            ApiInspectionResult inspection
    ) {
        if (!inspection.connectivityChecked()) {
            return skipped(
                    "api-http-status",
                    "API HTTP Status",
                    "Live API validation disabled"
            );
        }

        boolean healthy =
                inspection.responseStatus() >= 200
                        && inspection.responseStatus() < 400;

        return check(
                "api-http-status",
                "API HTTP Status",
                healthy,
                "HTTP status is between 200 and 399",
                String.valueOf(inspection.responseStatus()),
                true,
                "Correct the API health endpoint or service failure."
        );
    }

    private DiagnosticCheck latencyCheck(
            ApiInspectionResult inspection
    ) {
        if (!inspection.connectivityChecked()) {
            return skipped(
                    "api-response-latency",
                    "API Response Latency",
                    "Live API validation disabled"
            );
        }

        long threshold = Long.getLong(
                "mapaf.api.health.max.latency.ms",
                2000L
        );

        boolean healthy =
                inspection.endpointReachable()
                        && inspection.responseTimeMillis()
                        <= threshold;

        return new DiagnosticCheck(
                "api-response-latency",
                "API Response Latency",
                healthy
                        ? DiagnosticStatus.PASS
                        : DiagnosticStatus.WARN,
                "Response time <= " + threshold + " ms",
                inspection.responseTimeMillis() + " ms",
                healthy
                        ? "API latency is within the Doctor threshold."
                        : "API latency exceeds the Doctor threshold.",
                healthy
                        ? "No corrective action required."
                        : "Investigate API latency, dependencies, "
                        + "or environment performance.",
                Map.of(
                        "critical",
                        false,
                        "thresholdMillis",
                        threshold
                )
        );
    }

    private DiagnosticCheck responseBodyCheck(
            ApiInspectionResult inspection
    ) {
        if (!inspection.connectivityChecked()) {
            return skipped(
                    "api-response-body",
                    "API Response Body",
                    "Live API validation disabled"
            );
        }

        return new DiagnosticCheck(
                "api-response-body",
                "API Response Body",
                inspection.responseBodyPresent()
                        ? DiagnosticStatus.PASS
                        : DiagnosticStatus.WARN,
                "Non-empty API response body",
                inspection.responseBodyPresent()
                        ? "Present"
                        : "Empty",
                inspection.responseBodyPresent()
                        ? "API response body is available."
                        : "API response body is empty.",
                inspection.responseBodyPresent()
                        ? "No corrective action required."
                        : "Review the API health endpoint response.",
                Map.of("critical", false)
        );
    }

    private DiagnosticCheck evidenceCheck(
            String id,
            String name,
            boolean available,
            boolean critical,
            String action
    ) {
        return check(
                id,
                name,
                available,
                "API evidence artifact is available",
                available ? "Available" : "Missing",
                critical,
                action
        );
    }

    private DiagnosticCheck skipped(
            String id,
            String name,
            String actual
    ) {
        return new DiagnosticCheck(
                id,
                name,
                DiagnosticStatus.SKIPPED,
                "Optional live API validation",
                actual,
                "Live API diagnostic was not executed.",
                "Enable live API health validation when required.",
                Map.of("critical", false)
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
                        ? "API diagnostic passed."
                        : "API diagnostic did not satisfy "
                        + "the governed health expectation.",
                passed
                        ? "No corrective action required."
                        : action,
                Map.of("critical", critical)
        );
    }

    private HealthStatus determineStatus(
            ApiInspectionResult inspection
    ) {
        if (!inspection.enterpriseSummaryAvailable()) {
            return HealthStatus.UNHEALTHY;
        }

        if (!inspection.connectivityChecked()) {
            return inspection.dashboardAvailable()
                    ? HealthStatus.HEALTHY
                    : HealthStatus.DEGRADED;
        }

        if (!inspection.endpointConfigured()
                || !inspection.authenticationReady()
                || !inspection.successfulResponse()) {
            return HealthStatus.UNHEALTHY;
        }

        long threshold = Long.getLong(
                "mapaf.api.health.max.latency.ms",
                2000L
        );

        if (inspection.responseTimeMillis() > threshold
                || !inspection.responseBodyPresent()
                || !inspection.dashboardAvailable()
                || !inspection.failureShowcaseAvailable()) {
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
                    "API automation and health evidence are operational.";
            case DEGRADED ->
                    "API automation is operational with non-critical "
                            + "health or evidence limitations.";
            case UNHEALTHY ->
                    "A critical API endpoint or evidence dependency "
                            + "is unavailable.";
            case NOT_CONFIGURED ->
                    "API health validation is not configured.";
        } + " " + passed + "/" + total + " checks passed.";
    }
}
