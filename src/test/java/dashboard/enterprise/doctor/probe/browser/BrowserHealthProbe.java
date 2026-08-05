package dashboard.enterprise.doctor.probe.browser;

import dashboard.enterprise.doctor.model.DiagnosticCheck;
import dashboard.enterprise.doctor.model.DiagnosticStatus;
import dashboard.enterprise.doctor.model.HealthProbeDefinition;
import dashboard.enterprise.doctor.model.HealthProbeResult;
import dashboard.enterprise.doctor.model.HealthSeverity;
import dashboard.enterprise.doctor.model.HealthStatus;
import dashboard.enterprise.doctor.probe.DoctorContext;
import dashboard.enterprise.doctor.probe.HealthProbe;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class BrowserHealthProbe implements HealthProbe {

    private final BrowserRuntime runtime;

    private final HealthProbeDefinition definition =
            new HealthProbeDefinition(
                    "browser-health",
                    "Browser Health",
                    "1.0",
                    HealthSeverity.HIGH,
                    true,
                    false,
                    "Validates Playwright, Chromium, headless launch, "
                            + "page rendering, screenshots, and traces.",
                    List.of("environment-health")
            );

    public BrowserHealthProbe() {
        this(new PlaywrightBrowserRuntime());
    }

    public BrowserHealthProbe(BrowserRuntime runtime) {
        if (runtime == null) {
            throw new IllegalArgumentException(
                    "Browser runtime is required."
            );
        }

        this.runtime = runtime;
    }

    @Override
    public HealthProbeDefinition definition() {
        return definition;
    }

    @Override
    public HealthProbeResult execute(DoctorContext context) {
        Path evidenceDirectory = context.repositoryRoot()
                .resolve("build/doctor/browser-health");

        BrowserRuntimeResult runtimeResult =
                runtime.inspect(
                        context.repositoryRoot(),
                        evidenceDirectory
                );

        List<DiagnosticCheck> checks = new ArrayList<>();

        checks.add(check(
                "playwright-runtime",
                "Playwright Runtime",
                runtimeResult.playwrightAvailable(),
                "Playwright Java runtime is available",
                runtimeResult.playwrightAvailable()
                        ? "Available"
                        : "Unavailable",
                true,
                "Restore the Playwright Java dependency."
        ));

        checks.add(check(
                "chromium-launch",
                "Chromium Headless Launch",
                runtimeResult.chromiumLaunchSucceeded(),
                "Chromium launches in headless mode",
                runtimeResult.chromiumLaunchSucceeded()
                        ? "Launched"
                        : "Launch failed",
                true,
                "Install the Playwright Chromium browser binaries."
        ));

        checks.add(check(
                "browser-page-render",
                "Browser Page Rendering",
                runtimeResult.pageRenderSucceeded(),
                "Browser renders the MAPAF diagnostic page",
                runtimeResult.pageRenderSucceeded()
                        ? "Rendered"
                        : "Render failed",
                true,
                "Review the browser launch logs and page-rendering failure."
        ));

        checks.add(check(
                "browser-screenshot",
                "Browser Screenshot",
                runtimeResult.screenshotSucceeded(),
                "Browser screenshot evidence is generated",
                runtimeResult.screenshotSucceeded()
                        ? "Generated"
                        : "Not generated",
                false,
                "Verify evidence-directory permissions and screenshot support."
        ));

        checks.add(check(
                "browser-trace",
                "Browser Trace",
                runtimeResult.traceSucceeded(),
                "Playwright trace evidence is generated",
                runtimeResult.traceSucceeded()
                        ? "Generated"
                        : "Not generated",
                false,
                "Verify Playwright tracing support and evidence permissions."
        ));

        int criticalFailures = 0;
        int warnings = 0;
        int passed = 0;

        for (DiagnosticCheck check : checks) {
            if (check.status() == DiagnosticStatus.PASS) {
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

        if (!runtimeResult.playwrightAvailable()) {
            status = HealthStatus.NOT_CONFIGURED;
        } else if (criticalFailures > 0) {
            status = HealthStatus.UNHEALTHY;
        } else if (warnings > 0) {
            status = HealthStatus.DEGRADED;
        } else {
            status = HealthStatus.HEALTHY;
        }

        List<String> actions = checks.stream()
                .filter(check ->
                        check.status() != DiagnosticStatus.PASS)
                .map(DiagnosticCheck::correctiveAction)
                .filter(value -> !value.isBlank())
                .distinct()
                .toList();

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put(
                "browserVersion",
                runtimeResult.browserVersion()
        );
        metadata.put("checksTotal", checks.size());
        metadata.put("checksPassed", passed);
        metadata.put("criticalFailures", criticalFailures);
        metadata.put("warnings", warnings);
        metadata.put(
                "evidenceDirectory",
                context.repositoryRoot()
                        .relativize(evidenceDirectory)
                        .toString()
        );

        return new HealthProbeResult(
                definition.id(),
                definition.name(),
                definition.version(),
                definition.severity(),
                status,
                runtimeResult.durationMillis(),
                summary(status, passed, checks.size()),
                runtimeResult.fullyHealthy()
                        ? "No browser health issues detected."
                        : runtimeResult.diagnosis(),
                actions,
                checks,
                runtimeResult.evidenceReferences(),
                metadata
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
                        ? "Browser diagnostic passed."
                        : "Browser diagnostic did not satisfy "
                        + "the governed health expectation.",
                passed
                        ? "No corrective action required."
                        : action,
                Map.of("critical", critical)
        );
    }

    private String summary(
            HealthStatus status,
            int passed,
            int total
    ) {
        return switch (status) {
            case HEALTHY ->
                    "Browser automation runtime is fully operational.";
            case DEGRADED ->
                    "Browser automation is operational, but evidence "
                            + "capabilities require attention.";
            case UNHEALTHY ->
                    "Browser automation cannot execute reliably.";
            case NOT_CONFIGURED ->
                    "Playwright browser automation is not configured.";
        } + " " + passed + "/" + total + " checks passed.";
    }
}
