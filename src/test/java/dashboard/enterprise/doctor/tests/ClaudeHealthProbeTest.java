package dashboard.enterprise.doctor.tests;

import dashboard.enterprise.doctor.model.HealthStatus;
import dashboard.enterprise.doctor.probe.DoctorContext;
import dashboard.enterprise.doctor.probe.claude.ClaudeHealthProbe;
import dashboard.enterprise.doctor.probe.claude.ClaudeInspectionResult;
import dashboard.enterprise.doctor.probe.claude.ClaudeInspector;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class ClaudeHealthProbeTest {

    @Test
    public void shouldPassHealthyLiveProvider() {
        ClaudeInspector inspector = context ->
                result(
                        true,
                        true,
                        true,
                        true,
                        true,
                        true,
                        8
                );

        var health = new ClaudeHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                health.status(),
                HealthStatus.HEALTHY
        );

        Assert.assertEquals(
                health.metadata().get("executionMode"),
                "LIVE_PROVIDER"
        );
    }

    @Test
    public void shouldDegradeToGovernedReplay() {
        ClaudeInspector inspector = context ->
                result(
                        false,
                        false,
                        true,
                        false,
                        false,
                        true,
                        8
                );

        var health = new ClaudeHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                health.status(),
                HealthStatus.DEGRADED
        );

        Assert.assertEquals(
                health.metadata().get("executionMode"),
                "GOVERNED_REPLAY"
        );
    }

    @Test
    public void shouldReportNotConfiguredWithoutProviderOrReplay() {
        ClaudeInspector inspector = context ->
                result(
                        false,
                        false,
                        true,
                        false,
                        false,
                        false,
                        0
                );

        var health = new ClaudeHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                health.status(),
                HealthStatus.NOT_CONFIGURED
        );
    }

    @Test
    public void shouldBecomeUnhealthyWhenEnabledProviderFails()
            {
        ClaudeInspector inspector = context ->
                result(
                        true,
                        true,
                        true,
                        true,
                        false,
                        false,
                        0
                );

        var health = new ClaudeHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                health.status(),
                HealthStatus.UNHEALTHY
        );
    }

    private ClaudeInspectionResult result(
            boolean enabled,
            boolean key,
            boolean endpoint,
            boolean connectivityChecked,
            boolean connectivityHealthy,
            boolean replay,
            int assets
    ) {
        return new ClaudeInspectionResult(
                enabled,
                key,
                endpoint,
                connectivityChecked,
                connectivityHealthy,
                replay,
                assets,
                "Claude",
                "https://api.anthropic.com",
                "Test Claude diagnostic result.",
                2,
                replay
                        ? List.of("generated/ai/automation")
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
}
