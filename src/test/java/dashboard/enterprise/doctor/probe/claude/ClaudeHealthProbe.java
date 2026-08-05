package dashboard.enterprise.doctor.probe.claude;

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

public final class ClaudeHealthProbe implements HealthProbe {

    private final ClaudeInspector inspector;

    private final HealthProbeDefinition definition =
            new HealthProbeDefinition(
                    "claude-health",
                    "Claude Health",
                    "1.0",
                    HealthSeverity.MEDIUM,
                    true,
                    true,
                    "Validates Claude provider configuration, "
                            + "optional connectivity, and governed "
                            + "AI-generation replay availability.",
                    List.of("environment-health")
            );

    public ClaudeHealthProbe() {
        this(new EnvironmentClaudeInspector());
    }

    public ClaudeHealthProbe(ClaudeInspector inspector) {
        if (inspector == null) {
            throw new IllegalArgumentException(
                    "Claude inspector is required."
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
        ClaudeInspectionResult inspection =
                inspector.inspect(context);

        List<DiagnosticCheck> checks = new ArrayList<>();

        checks.add(check(
                "claude-provider-enabled",
                "Claude Provider",
                inspection.providerEnabled(),
                "Live provider is enabled",
                inspection.providerEnabled()
                        ? "Enabled"
                        : "Disabled",
                false,
                "Set CLAUDE_ENABLED=true to enable live generation."
        ));

        checks.add(check(
                "claude-api-key",
                "Claude API Credential",
                inspection.apiKeyConfigured(),
                "API credential is configured",
                inspection.apiKeyConfigured()
                        ? "Configured"
                        : "Not configured",
                false,
                "Configure ANTHROPIC_API_KEY or CLAUDE_API_KEY."
        ));

        checks.add(check(
                "claude-endpoint",
                "Claude Endpoint",
                inspection.endpointConfigured(),
                "Provider endpoint is configured",
                inspection.endpointConfigured()
                        ? inspection.endpoint()
                        : "Not configured",
                false,
                "Configure the Claude provider endpoint."
        ));

        checks.add(connectivityCheck(inspection));

        checks.add(check(
                "claude-governed-replay",
                "Governed AI Replay",
                inspection.governedReplayAvailable(),
                "Governed generated automation assets are available",
                inspection.governedReplayAvailable()
                        ? inspection.governedAssets()
                        + " asset(s)"
                        : "No replay assets",
                true,
                "Generate or restore governed AI automation assets."
        ));

        HealthStatus status;

        if (inspection.liveProviderHealthy()) {
            status = HealthStatus.HEALTHY;
        } else if (inspection.governedReplayAvailable()) {
            status = HealthStatus.DEGRADED;
        } else if (!inspection.providerEnabled()) {
            status = HealthStatus.NOT_CONFIGURED;
        } else {
            status = HealthStatus.UNHEALTHY;
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

        int passed = (int) checks.stream()
                .filter(check ->
                        check.status() == DiagnosticStatus.PASS
                                || check.status()
                                == DiagnosticStatus.SKIPPED)
                .count();

        Map<String, Object> metadata =
                new LinkedHashMap<>(inspection.metadata());

        metadata.put("provider", inspection.provider());
        metadata.put("endpoint", inspection.endpoint());
        metadata.put(
                "executionMode",
                inspection.liveProviderHealthy()
                        ? "LIVE_PROVIDER"
                        : inspection.governedReplayAvailable()
                        ? "GOVERNED_REPLAY"
                        : "UNAVAILABLE"
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

    private DiagnosticCheck connectivityCheck(
            ClaudeInspectionResult inspection
    ) {
        if (!inspection.liveConnectivityChecked()) {
            return new DiagnosticCheck(
                    "claude-connectivity",
                    "Claude Connectivity",
                    DiagnosticStatus.SKIPPED,
                    "Optional live-provider connectivity check",
                    "Disabled",
                    "Live Claude connectivity validation was not enabled.",
                    "Set -Dmapaf.claude.connectivity.enabled=true "
                            + "when network validation is required.",
                    Map.of("critical", false)
            );
        }

        return check(
                "claude-connectivity",
                "Claude Connectivity",
                inspection.liveConnectivityHealthy(),
                "Provider endpoint is reachable",
                inspection.liveConnectivityHealthy()
                        ? "Reachable"
                        : "Unreachable",
                false,
                "Validate Claude network access and credentials."
        );
    }

    private DiagnosticCheck check(
            String id,
            String name,
            boolean passed,
            String expected,
            String actual,
            boolean replayCritical,
            String action
    ) {
        return new DiagnosticCheck(
                id,
                name,
                passed
                        ? DiagnosticStatus.PASS
                        : replayCritical
                        ? DiagnosticStatus.FAIL
                        : DiagnosticStatus.WARN,
                expected,
                actual,
                passed
                        ? "Claude diagnostic passed."
                        : "Claude capability is not fully available.",
                passed
                        ? "No corrective action required."
                        : action,
                Map.of("replayCritical", replayCritical)
        );
    }

    private String summary(
            HealthStatus status,
            int passed,
            int total
    ) {
        return switch (status) {
            case HEALTHY ->
                    "Claude live-provider integration is operational.";
            case DEGRADED ->
                    "Claude live provider is unavailable, but governed "
                            + "AI replay remains operational.";
            case UNHEALTHY ->
                    "Claude execution and governed replay are unavailable.";
            case NOT_CONFIGURED ->
                    "Claude integration is not configured.";
        } + " " + passed + "/" + total + " checks passed.";
    }
}
