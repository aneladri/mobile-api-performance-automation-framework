package dashboard.enterprise.doctor.tests;

import dashboard.enterprise.doctor.model.HealthStatus;
import dashboard.enterprise.doctor.probe.DoctorContext;
import dashboard.enterprise.doctor.probe.performance.PerformanceHealthProbe;
import dashboard.enterprise.doctor.probe.performance.PerformanceInspectionResult;
import dashboard.enterprise.doctor.probe.performance.PerformanceInspector;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class PerformanceHealthProbeTest {

    @Test
    public void shouldPassHealthyPerformanceEnvironment() {
        PerformanceInspector inspector = context ->
                result(
                        true,
                        true,
                        true,
                        true,
                        true,
                        true,
                        10000,
                        0.2,
                        450,
                        true
                );

        var health = new PerformanceHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                health.status(),
                HealthStatus.HEALTHY
        );
    }

    @Test
    public void shouldDegradeWhenProductionEvidenceIsMissing() {
        PerformanceInspector inspector = context ->
                result(
                        true,
                        true,
                        true,
                        true,
                        true,
                        false,
                        1000,
                        0.1,
                        300,
                        true
                );

        var health = new PerformanceHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                health.status(),
                HealthStatus.DEGRADED
        );
    }

    @Test
    public void shouldDegradeWhenToolsAreUnavailableButEvidencePasses() {
        PerformanceInspector inspector = context ->
                result(
                        false,
                        false,
                        true,
                        true,
                        true,
                        true,
                        1000,
                        0.1,
                        300,
                        true
                );

        var health = new PerformanceHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                health.status(),
                HealthStatus.DEGRADED
        );
    }

    @Test
    public void shouldFailWhenErrorRateExceedsThreshold() {
        PerformanceInspector inspector = context ->
                result(
                        true,
                        true,
                        true,
                        true,
                        true,
                        true,
                        1000,
                        1.5,
                        300,
                        true
                );

        var health = new PerformanceHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                health.status(),
                HealthStatus.UNHEALTHY
        );
    }

    @Test
    public void shouldFailWhenP95LatencyExceedsThreshold() {
        PerformanceInspector inspector = context ->
                result(
                        true,
                        true,
                        true,
                        true,
                        true,
                        true,
                        1000,
                        0.1,
                        1500,
                        true
                );

        var health = new PerformanceHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                health.status(),
                HealthStatus.UNHEALTHY
        );
    }

    @Test
    public void shouldFailWhenEnterpriseSummaryIsMissing() {
        PerformanceInspector inspector = context ->
                result(
                        true,
                        true,
                        false,
                        true,
                        true,
                        true,
                        0,
                        0,
                        0,
                        false
                );

        var health = new PerformanceHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                health.status(),
                HealthStatus.UNHEALTHY
        );
    }

    private PerformanceInspectionResult result(
            boolean k6,
            boolean jmeter,
            boolean summary,
            boolean dashboard,
            boolean failure,
            boolean production,
            long requests,
            double errorRate,
            double p95,
            boolean workloadPassed
    ) {
        return new PerformanceInspectionResult(
                k6,
                jmeter,
                summary,
                dashboard,
                failure,
                production,
                requests,
                errorRate,
                p95,
                1.0,
                1000,
                workloadPassed,
                "Test performance diagnostic result.",
                4,
                summary
                        ? List.of(
                                "performance/reports/"
                                        + "enterprise-summary.json"
                        )
                        : List.of(),
                Map.of()
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
    @Test
    public void shouldRecognizeProductionPerformanceContract() {
        PerformanceInspector inspector = context ->
                result(
                        true,
                        true,
                        true,
                        true,
                        true,
                        true,
                        10500,
                        0.01904761904761905,
                        178.9047619047619,
                        true
                );

        var health = new PerformanceHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                health.status(),
                HealthStatus.HEALTHY
        );

        Assert.assertEquals(
                health.metadata().get("totalRequests"),
                10500L
        );

        Assert.assertEquals(
                (Double) health.metadata().get("p95LatencyMillis"),
                178.9047619047619,
                0.0001
        );
    }

}
