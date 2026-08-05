package dashboard.enterprise.doctor.tests;

import dashboard.enterprise.doctor.model.HealthStatus;
import dashboard.enterprise.doctor.probe.DoctorContext;
import dashboard.enterprise.doctor.probe.device.DeviceHealthProbe;
import dashboard.enterprise.doctor.probe.device.DeviceInspector;
import dashboard.enterprise.doctor.probe.device.DeviceInventoryResult;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class DeviceHealthProbeTest {

    @Test
    public void shouldPassRequiredAndroidExecution() {
        DeviceInspector inspector = context ->
                result(
                        "ANDROID_REQUIRED",
                        true,
                        1,
                        1,
                        0,
                        true,
                        true,
                        true,
                        false,
                        0,
                        true
                );

        var health = new DeviceHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                health.status(),
                HealthStatus.HEALTHY
        );

        Assert.assertEquals(
                health.metadata().get("androidReady"),
                true
        );
    }

    @Test
    public void shouldFailRequiredAndroidWithoutDevice() {
        DeviceInspector inspector = context ->
                result(
                        "ANDROID_REQUIRED",
                        true,
                        0,
                        0,
                        0,
                        true,
                        false,
                        true,
                        false,
                        0,
                        true
                );

        var health = new DeviceHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                health.status(),
                HealthStatus.UNHEALTHY
        );
    }

    @Test
    public void shouldPassRequiredIosExecution() {
        DeviceInspector inspector = context ->
                result(
                        "IOS_REQUIRED",
                        false,
                        0,
                        0,
                        0,
                        true,
                        false,
                        true,
                        true,
                        1,
                        true
                );

        var health = new DeviceHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                health.status(),
                HealthStatus.HEALTHY
        );

        Assert.assertEquals(
                health.metadata().get("iosReady"),
                true
        );
    }

    @Test
    public void shouldDegradeOptionalDeviceEnvironment() {
        DeviceInspector inspector = context ->
                result(
                        "OPTIONAL",
                        true,
                        0,
                        0,
                        0,
                        true,
                        false,
                        true,
                        true,
                        0,
                        true
                );

        var health = new DeviceHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                health.status(),
                HealthStatus.DEGRADED
        );
    }

    @Test
    public void shouldReportNotConfiguredWhenNoMobileCapabilityExists() {
        DeviceInspector inspector = context ->
                result(
                        "OPTIONAL",
                        false,
                        0,
                        0,
                        0,
                        false,
                        false,
                        false,
                        false,
                        0,
                        false
                );

        var health = new DeviceHealthProbe(inspector)
                .execute(context());

        Assert.assertEquals(
                health.status(),
                HealthStatus.NOT_CONFIGURED
        );
    }

    private DeviceInventoryResult result(
            String mode,
            boolean adb,
            int androidDevices,
            int authorized,
            int unauthorized,
            boolean appium,
            boolean serverCheck,
            boolean serverReachable,
            boolean simctl,
            int iosBooted,
            boolean evidence
    ) {
        return new DeviceInventoryResult(
                mode,
                adb,
                androidDevices,
                authorized,
                unauthorized,
                appium,
                serverCheck,
                serverReachable,
                "http://127.0.0.1:4723/status",
                simctl,
                iosBooted,
                evidence,
                "Test OS",
                "Test device diagnostic result.",
                3,
                authorized > 0
                        ? List.of("android-test-device")
                        : List.of(),
                iosBooted > 0
                        ? List.of("ios-test-simulator")
                        : List.of(),
                evidence
                        ? List.of("mobile/reports")
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
