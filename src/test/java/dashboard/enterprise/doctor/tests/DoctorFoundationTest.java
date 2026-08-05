package dashboard.enterprise.doctor.tests;

import dashboard.enterprise.doctor.engine.DefaultDoctorEngine;
import dashboard.enterprise.doctor.model.DiagnosticCheck;
import dashboard.enterprise.doctor.model.HealthProbeDefinition;
import dashboard.enterprise.doctor.model.HealthProbeResult;
import dashboard.enterprise.doctor.model.HealthSeverity;
import dashboard.enterprise.doctor.model.HealthStatus;
import dashboard.enterprise.doctor.probe.DoctorContext;
import dashboard.enterprise.doctor.probe.HealthProbe;
import dashboard.enterprise.doctor.probe.InMemoryHealthProbeRegistry;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class DoctorFoundationTest {

    @Test
    public void shouldProduceHealthyDoctorSnapshot() {
        InMemoryHealthProbeRegistry registry =
                new InMemoryHealthProbeRegistry();

        registry.register(
                probe(
                        "healthy-foundation",
                        HealthSeverity.CRITICAL,
                        HealthStatus.HEALTHY
                )
        );

        var snapshot = new DefaultDoctorEngine(registry)
                .assess(context());

        Assert.assertEquals(
                snapshot.schemaVersion(),
                "mapaf.doctor/v1"
        );

        Assert.assertEquals(
                snapshot.overallStatus(),
                HealthStatus.HEALTHY
        );

        Assert.assertEquals(snapshot.healthScore(), 100);
        Assert.assertTrue(snapshot.platformReady());
        Assert.assertEquals(snapshot.totalProbes(), 1);
    }

    @Test
    public void shouldDegradeForNonCriticalUnhealthyProbe() {
        InMemoryHealthProbeRegistry registry =
                new InMemoryHealthProbeRegistry();

        registry.register(
                probe(
                        "optional-integration",
                        HealthSeverity.LOW,
                        HealthStatus.UNHEALTHY
                )
        );

        var snapshot = new DefaultDoctorEngine(registry)
                .assess(context());

        Assert.assertEquals(
                snapshot.overallStatus(),
                HealthStatus.DEGRADED
        );

        Assert.assertTrue(snapshot.platformReady());
    }

    @Test
    public void shouldBecomeUnhealthyForCriticalFailure() {
        InMemoryHealthProbeRegistry registry =
                new InMemoryHealthProbeRegistry();

        registry.register(
                probe(
                        "critical-runtime",
                        HealthSeverity.CRITICAL,
                        HealthStatus.UNHEALTHY
                )
        );

        var snapshot = new DefaultDoctorEngine(registry)
                .assess(context());

        Assert.assertEquals(
                snapshot.overallStatus(),
                HealthStatus.UNHEALTHY
        );

        Assert.assertFalse(snapshot.platformReady());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldRejectDuplicateProbeRegistration() {
        InMemoryHealthProbeRegistry registry =
                new InMemoryHealthProbeRegistry();

        registry.register(
                probe(
                        "duplicate",
                        HealthSeverity.INFO,
                        HealthStatus.HEALTHY
                )
        );

        registry.register(
                probe(
                        "duplicate",
                        HealthSeverity.INFO,
                        HealthStatus.HEALTHY
                )
        );
    }

    private DoctorContext context() {
        return new DoctorContext(
                Path.of("."),
                "2.9.0",
                "TEST",
                Map.of(),
                Map.of()
        );
    }

    private HealthProbe probe(
            String id,
            HealthSeverity severity,
            HealthStatus status
    ) {
        return new HealthProbe() {
            private final HealthProbeDefinition definition =
                    new HealthProbeDefinition(
                            id,
                            id,
                            "1.0",
                            severity,
                            true,
                            false,
                            "Test health probe",
                            List.of()
                    );

            @Override
            public HealthProbeDefinition definition() {
                return definition;
            }

            @Override
            public HealthProbeResult execute(
                    DoctorContext context
            ) {
                return new HealthProbeResult(
                        definition.id(),
                        definition.name(),
                        definition.version(),
                        definition.severity(),
                        status,
                        1,
                        "Test probe result",
                        status == HealthStatus.HEALTHY
                                ? "No issue detected."
                                : "Test diagnostic issue.",
                        status == HealthStatus.HEALTHY
                                ? List.of()
                                : List.of(
                                        "Apply the test corrective action."
                                ),
                        List.of(
                                DiagnosticCheck.pass(
                                        id + "-check",
                                        "Test check",
                                        "Operational",
                                        status.name()
                                )
                        ),
                        List.of(),
                        Map.of()
                );
            }
        };
    }
}
