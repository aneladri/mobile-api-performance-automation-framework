package dashboard.enterprise.doctor.tests;

import dashboard.enterprise.doctor.model.HealthStatus;
import dashboard.enterprise.doctor.probe.DoctorContext;
import dashboard.enterprise.doctor.probe.api.ApiHealthProbe;
import dashboard.enterprise.doctor.probe.api.ApiInspectionResult;
import dashboard.enterprise.doctor.probe.api.ApiInspector;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class ApiHealthProbeTest {

    @Test
    public void shouldPassHealthyLiveApi() {
        ApiInspector inspector = context ->
                result(
                        true,
                        true,
                        true,
                        200,
                        120,
                        true,
                        false,
                        true,
                        true,
                        true,
                        true
                );

        var health = new ApiHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                health.status(),
                HealthStatus.HEALTHY
        );
    }

    @Test
    public void shouldPassEvidenceOnlyMode() {
        ApiInspector inspector = context ->
                result(
                        false,
                        false,
                        false,
                        0,
                        0,
                        false,
                        false,
                        true,
                        true,
                        true,
                        true
                );

        var health = new ApiHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                health.status(),
                HealthStatus.HEALTHY
        );
    }

    @Test
    public void shouldFailWhenEndpointIsUnreachable() {
        ApiInspector inspector = context ->
                result(
                        true,
                        true,
                        false,
                        0,
                        1500,
                        false,
                        false,
                        true,
                        true,
                        true,
                        true
                );

        var health = new ApiHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                health.status(),
                HealthStatus.UNHEALTHY
        );
    }

    @Test
    public void shouldFailWhenAuthenticationIsMissing() {
        ApiInspector inspector = context ->
                result(
                        true,
                        true,
                        true,
                        401,
                        100,
                        true,
                        true,
                        false,
                        true,
                        true,
                        true
                );

        var health = new ApiHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                health.status(),
                HealthStatus.UNHEALTHY
        );
    }

    @Test
    public void shouldDegradeWhenLatencyExceedsThreshold() {
        ApiInspector inspector = context ->
                result(
                        true,
                        true,
                        true,
                        200,
                        2501,
                        true,
                        false,
                        true,
                        true,
                        true,
                        true
                );

        var health = new ApiHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                health.status(),
                HealthStatus.DEGRADED
        );
    }

    @Test
    public void shouldFailWhenEnterpriseSummaryIsMissing() {
        ApiInspector inspector = context ->
                result(
                        false,
                        false,
                        false,
                        0,
                        0,
                        false,
                        false,
                        true,
                        false,
                        true,
                        true
                );

        var health = new ApiHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                health.status(),
                HealthStatus.UNHEALTHY
        );
    }

    private ApiInspectionResult result(
            boolean endpointConfigured,
            boolean connectivityChecked,
            boolean reachable,
            int status,
            long latency,
            boolean body,
            boolean authRequired,
            boolean authConfigured,
            boolean summary,
            boolean dashboard,
            boolean failureShowcase
    ) {
        return new ApiInspectionResult(
                endpointConfigured,
                connectivityChecked,
                reachable,
                status,
                latency,
                body,
                authRequired,
                authConfigured,
                summary,
                dashboard,
                failureShowcase,
                endpointConfigured
                        ? "http://localhost:8081/health"
                        : "",
                "GET",
                "Test API diagnostic result.",
                3,
                summary
                        ? List.of(
                                "api/reports/enterprise-summary.json"
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
}
