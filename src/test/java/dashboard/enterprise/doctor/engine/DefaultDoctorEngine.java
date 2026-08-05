package dashboard.enterprise.doctor.engine;

import dashboard.enterprise.doctor.model.DoctorSnapshot;
import dashboard.enterprise.doctor.model.HealthProbeResult;
import dashboard.enterprise.doctor.model.HealthStatus;
import dashboard.enterprise.doctor.probe.DoctorContext;
import dashboard.enterprise.doctor.probe.HealthProbe;
import dashboard.enterprise.doctor.probe.HealthProbeRegistry;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class DefaultDoctorEngine implements DoctorEngine {

    private final HealthProbeRegistry registry;

    public DefaultDoctorEngine(HealthProbeRegistry registry) {
        if (registry == null) {
            throw new IllegalArgumentException(
                    "Health probe registry is required."
            );
        }

        this.registry = registry;
    }

    @Override
    public DoctorSnapshot assess(DoctorContext context) {
        List<HealthProbeResult> results = new ArrayList<>();

        for (HealthProbe probe : registry.enabledProbes()) {
            results.add(executeSafely(probe, context));
        }

        int healthy = count(results, HealthStatus.HEALTHY);
        int degraded = count(results, HealthStatus.DEGRADED);
        int unhealthy = count(results, HealthStatus.UNHEALTHY);
        int notConfigured = count(
                results,
                HealthStatus.NOT_CONFIGURED
        );

        boolean criticalFailure = results.stream()
                .anyMatch(HealthProbeResult::blocksPlatformReadiness);

        HealthStatus overall = overallStatus(
                results,
                criticalFailure
        );

        int score = healthScore(results);

        List<String> diagnoses = results.stream()
                .filter(result ->
                        result.status() != HealthStatus.HEALTHY)
                .map(result ->
                        result.probeName()
                                + ": "
                                + result.diagnosis())
                .filter(value -> !value.endsWith(": "))
                .toList();

        List<String> actions = results.stream()
                .flatMap(result ->
                        result.correctiveActions().stream())
                .distinct()
                .toList();

        boolean ready = !criticalFailure
                && overall != HealthStatus.UNHEALTHY;

        return new DoctorSnapshot(
                "mapaf.doctor/v1",
                Instant.now().toString(),
                "MAPAF Enterprise Quality Engineering Platform",
                context.platformVersion(),
                context.environment(),
                overall,
                score,
                ready,
                results.size(),
                healthy,
                degraded,
                unhealthy,
                notConfigured,
                executiveSummary(overall, criticalFailure),
                diagnoses,
                actions,
                results
        );
    }

    private HealthProbeResult executeSafely(
            HealthProbe probe,
            DoctorContext context
    ) {
        long started = System.nanoTime();

        try {
            return probe.execute(context);
        } catch (Exception exception) {
            long duration = elapsedMillis(started);

            return new HealthProbeResult(
                    probe.definition().id(),
                    probe.definition().name(),
                    probe.definition().version(),
                    probe.definition().severity(),
                    HealthStatus.UNHEALTHY,
                    duration,
                    "Health probe execution failed.",
                    exception.getClass().getSimpleName()
                            + ": "
                            + safeMessage(exception),
                    List.of(
                            "Review the probe logs and restore the "
                                    + probe.definition().name()
                                    + " dependency."
                    ),
                    List.of(),
                    List.of(),
                    java.util.Map.of(
                            "exceptionType",
                            exception.getClass().getName()
                    )
            );
        }
    }

    private int count(
            List<HealthProbeResult> results,
            HealthStatus status
    ) {
        return (int) results.stream()
                .filter(result -> result.status() == status)
                .count();
    }

    private HealthStatus overallStatus(
            List<HealthProbeResult> results,
            boolean criticalFailure
    ) {
        if (results.isEmpty()) {
            return HealthStatus.NOT_CONFIGURED;
        }

        if (criticalFailure) {
            return HealthStatus.UNHEALTHY;
        }

        boolean anyUnhealthy = results.stream()
                .anyMatch(result ->
                        result.status() == HealthStatus.UNHEALTHY);

        boolean anyDegraded = results.stream()
                .anyMatch(result ->
                        result.status() == HealthStatus.DEGRADED);

        boolean anyNotConfigured = results.stream()
                .anyMatch(result ->
                        result.status()
                                == HealthStatus.NOT_CONFIGURED);

        if (anyUnhealthy || anyDegraded || anyNotConfigured) {
            return HealthStatus.DEGRADED;
        }

        return HealthStatus.HEALTHY;
    }

    private int healthScore(List<HealthProbeResult> results) {
        if (results.isEmpty()) {
            return 0;
        }

        double total = results.stream()
                .mapToDouble(result -> switch (result.status()) {
                    case HEALTHY -> 100.0;
                    case DEGRADED -> 70.0;
                    case NOT_CONFIGURED -> 60.0;
                    case UNHEALTHY -> 0.0;
                })
                .sum();

        return (int) Math.round(total / results.size());
    }

    private String executiveSummary(
            HealthStatus status,
            boolean criticalFailure
    ) {
        return switch (status) {
            case HEALTHY ->
                    "All enabled MAPAF platform health probes passed.";
            case DEGRADED ->
                    "MAPAF is operational, but one or more platform "
                            + "capabilities require attention.";
            case UNHEALTHY ->
                    criticalFailure
                            ? "A critical MAPAF dependency prevents "
                            + "reliable platform operation."
                            : "One or more MAPAF dependencies are unhealthy.";
            case NOT_CONFIGURED ->
                    "No MAPAF Doctor health probes are currently enabled.";
        };
    }

    private long elapsedMillis(long started) {
        return Math.max(
                0,
                (System.nanoTime() - started) / 1_000_000
        );
    }

    private String safeMessage(Exception exception) {
        return exception.getMessage() == null
                ? "No exception message was provided."
                : exception.getMessage();
    }
}
