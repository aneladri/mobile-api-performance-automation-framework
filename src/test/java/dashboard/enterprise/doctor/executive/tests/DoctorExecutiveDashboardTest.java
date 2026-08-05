package dashboard.enterprise.doctor.executive.tests;

import dashboard.enterprise.doctor.executive.publisher.DoctorExecutiveMapper;
import dashboard.enterprise.doctor.model.DoctorSnapshot;
import dashboard.enterprise.doctor.model.HealthProbeResult;
import dashboard.enterprise.doctor.model.HealthSeverity;
import dashboard.enterprise.doctor.model.HealthStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public final class DoctorExecutiveDashboardTest {

    @Test
    public void shouldCreateExecutiveOverviewContract() {
        var overview = new DoctorExecutiveMapper()
                .map(snapshot(
                        healthy(
                                "repository-foundation",
                                "Repository Foundation",
                                HealthSeverity.CRITICAL
                        ),
                        healthy(
                                "environment-health",
                                "Environment Health",
                                HealthSeverity.CRITICAL
                        ),
                        healthy(
                                "api-health",
                                "API Health",
                                HealthSeverity.CRITICAL
                        ),
                        healthy(
                                "performance-health",
                                "Performance Health",
                                HealthSeverity.HIGH
                        )
                ));

        Assert.assertEquals(
                overview.schemaVersion(),
                "mapaf.doctor.executive/v1"
        );

        Assert.assertEquals(
                overview.overallStatus(),
                HealthStatus.HEALTHY
        );

        Assert.assertEquals(
                overview.weightedHealthScore(),
                100
        );

        Assert.assertTrue(overview.platformReady());
    }

    @Test
    public void shouldWeightCriticalCapabilitiesMoreHighly() {
        HealthProbeResult repository =
                healthy(
                        "repository-foundation",
                        "Repository Foundation",
                        HealthSeverity.CRITICAL
                );

        HealthProbeResult claude =
                result(
                        "claude-health",
                        "Claude Health",
                        HealthSeverity.MEDIUM,
                        HealthStatus.DEGRADED
                );

        var overview = new DoctorExecutiveMapper()
                .map(snapshot(repository, claude));

        Assert.assertTrue(
                overview.weightedHealthScore() > 70
        );

        Assert.assertEquals(
                overview.warnings(),
                1
        );
    }

    @Test
    public void shouldExposeBlockingIssue() {
        HealthProbeResult repository =
                result(
                        "repository-foundation",
                        "Repository Foundation",
                        HealthSeverity.CRITICAL,
                        HealthStatus.UNHEALTHY
                );

        var overview = new DoctorExecutiveMapper()
                .map(snapshot(repository));

        Assert.assertEquals(
                overview.blockingIssues(),
                1
        );

        Assert.assertFalse(overview.platformReady());
    }

    @Test
    public void shouldExposeCapabilityReadiness() {
        var overview = new DoctorExecutiveMapper()
                .map(snapshot(
                        healthy(
                                "repository-foundation",
                                "Repository Foundation",
                                HealthSeverity.CRITICAL
                        ),
                        healthy(
                                "environment-health",
                                "Environment Health",
                                HealthSeverity.CRITICAL
                        ),
                        healthy(
                                "dashboard-health",
                                "Dashboard Health",
                                HealthSeverity.HIGH
                        ),
                        healthy(
                                "claude-health",
                                "Claude Health",
                                HealthSeverity.MEDIUM
                        ),
                        healthy(
                                "device-health",
                                "Device Health",
                                HealthSeverity.HIGH
                        ),
                        healthy(
                                "api-health",
                                "API Health",
                                HealthSeverity.CRITICAL
                        ),
                        healthy(
                                "performance-health",
                                "Performance Health",
                                HealthSeverity.HIGH
                        )
                ));

        Assert.assertTrue(
                overview.readiness().executionReady()
        );

        Assert.assertTrue(
                overview.readiness().releaseReady()
        );

        Assert.assertTrue(
                overview.readiness().aiReady()
        );

        Assert.assertTrue(
                overview.readiness().mobileReady()
        );
    }

    private DoctorSnapshot snapshot(
            HealthProbeResult... results
    ) {
        List<HealthProbeResult> probes =
                List.of(results);

        int healthy = (int) probes.stream()
                .filter(result ->
                        result.status()
                                == HealthStatus.HEALTHY)
                .count();

        int degraded = (int) probes.stream()
                .filter(result ->
                        result.status()
                                == HealthStatus.DEGRADED)
                .count();

        int unhealthy = (int) probes.stream()
                .filter(result ->
                        result.status()
                                == HealthStatus.UNHEALTHY)
                .count();

        int notConfigured = (int) probes.stream()
                .filter(result ->
                        result.status()
                                == HealthStatus.NOT_CONFIGURED)
                .count();

        boolean ready = probes.stream()
                .noneMatch(
                        HealthProbeResult::blocksPlatformReadiness
                );

        HealthStatus overall = unhealthy > 0
                ? ready
                ? HealthStatus.DEGRADED
                : HealthStatus.UNHEALTHY
                : degraded > 0
                || notConfigured > 0
                ? HealthStatus.DEGRADED
                : HealthStatus.HEALTHY;

        return new DoctorSnapshot(
                "mapaf.doctor/v1",
                "2026-08-02T00:00:00Z",
                "MAPAF Enterprise",
                "2.9.0",
                "TEST",
                overall,
                ready ? 90 : 40,
                ready,
                probes.size(),
                healthy,
                degraded,
                unhealthy,
                notConfigured,
                "Test Doctor snapshot",
                List.of(),
                List.of(),
                probes
        );
    }

    private HealthProbeResult healthy(
            String id,
            String name,
            HealthSeverity severity
    ) {
        return result(
                id,
                name,
                severity,
                HealthStatus.HEALTHY
        );
    }

    private HealthProbeResult result(
            String id,
            String name,
            HealthSeverity severity,
            HealthStatus status
    ) {
        return new HealthProbeResult(
                id,
                name,
                "1.0",
                severity,
                status,
                1,
                "Test probe summary",
                status == HealthStatus.HEALTHY
                        ? "No issue detected."
                        : "Test issue detected.",
                status == HealthStatus.HEALTHY
                        ? List.of()
                        : List.of("Apply test remediation."),
                List.of(),
                List.of(),
                Map.of()
        );
    }
}
