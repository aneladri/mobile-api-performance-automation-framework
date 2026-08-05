package dashboard.enterprise.doctor.tests;

import dashboard.enterprise.doctor.model.HealthStatus;
import dashboard.enterprise.doctor.probe.DoctorContext;
import dashboard.enterprise.doctor.probe.browser.BrowserHealthProbe;
import dashboard.enterprise.doctor.probe.browser.BrowserRuntime;
import dashboard.enterprise.doctor.probe.browser.BrowserRuntimeResult;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class BrowserHealthProbeTest {

    @Test
    public void shouldPassWhenBrowserRuntimeIsHealthy() {
        BrowserRuntime runtime = (
                root,
                evidence
        ) -> new BrowserRuntimeResult(
                true,
                true,
                true,
                true,
                true,
                "Chromium Test",
                "No browser issue detected.",
                5,
                List.of(
                        "build/doctor/browser-health/"
                                + "browser-health-screenshot.png",
                        "build/doctor/browser-health/"
                                + "browser-health-trace.zip"
                )
        );

        var result = new BrowserHealthProbe(runtime)
                .execute(context());

        Assert.assertEquals(
                result.status(),
                HealthStatus.HEALTHY
        );

        Assert.assertEquals(result.checks().size(), 5);
        Assert.assertEquals(
                result.evidenceReferences().size(),
                2
        );
    }

    @Test
    public void shouldBecomeUnhealthyWhenChromiumCannotLaunch() {
        BrowserRuntime runtime = (
                root,
                evidence
        ) -> new BrowserRuntimeResult(
                true,
                false,
                false,
                false,
                false,
                "",
                "Chromium executable is missing.",
                5,
                List.of()
        );

        var result = new BrowserHealthProbe(runtime)
                .execute(context());

        Assert.assertEquals(
                result.status(),
                HealthStatus.UNHEALTHY
        );
    }

    @Test
    public void shouldDegradeWhenScreenshotOrTraceFails() {
        BrowserRuntime runtime = (
                root,
                evidence
        ) -> new BrowserRuntimeResult(
                true,
                true,
                true,
                false,
                false,
                "Chromium Test",
                "Evidence publishing failed.",
                5,
                List.of()
        );

        var result = new BrowserHealthProbe(runtime)
                .execute(context());

        Assert.assertEquals(
                result.status(),
                HealthStatus.DEGRADED
        );
    }

    @Test
    public void shouldReportNotConfiguredWhenPlaywrightIsMissing() {
        BrowserRuntime runtime = (
                root,
                evidence
        ) -> new BrowserRuntimeResult(
                false,
                false,
                false,
                false,
                false,
                "",
                "Playwright is not available.",
                1,
                List.of()
        );

        var result = new BrowserHealthProbe(runtime)
                .execute(context());

        Assert.assertEquals(
                result.status(),
                HealthStatus.NOT_CONFIGURED
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
