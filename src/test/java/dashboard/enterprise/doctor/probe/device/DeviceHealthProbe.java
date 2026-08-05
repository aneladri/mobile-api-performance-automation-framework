package dashboard.enterprise.doctor.probe.device;

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

public final class DeviceHealthProbe implements HealthProbe {

    private final DeviceInspector inspector;

    private final HealthProbeDefinition definition =
            new HealthProbeDefinition(
                    "device-health",
                    "Device Health",
                    "1.0",
                    HealthSeverity.HIGH,
                    true,
                    true,
                    "Validates Android, iOS, Appium, device inventory, "
                            + "authorization, and mobile evidence readiness.",
                    List.of("environment-health")
            );

    public DeviceHealthProbe() {
        this(new SystemDeviceInspector());
    }

    public DeviceHealthProbe(
            DeviceInspector inspector
    ) {
        if (inspector == null) {
            throw new IllegalArgumentException(
                    "Device inspector is required."
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
        DeviceInventoryResult inventory =
                inspector.inspect(context);

        boolean androidRequired =
                "ANDROID_REQUIRED".equals(
                        inventory.executionMode()
                );

        boolean iosRequired =
                "IOS_REQUIRED".equals(
                        inventory.executionMode()
                );

        boolean anyRequired =
                "ANY_DEVICE_REQUIRED".equals(
                        inventory.executionMode()
                );

        boolean deviceRequired =
                androidRequired
                        || iosRequired
                        || anyRequired;

        List<DiagnosticCheck> checks =
                new ArrayList<>();

        checks.add(check(
                "device-adb",
                "Android Debug Bridge",
                inventory.adbAvailable(),
                "ADB is installed and executable",
                inventory.adbAvailable()
                        ? "Available"
                        : "Unavailable",
                androidRequired,
                "Install Android Platform Tools and add adb to PATH."
        ));

        checks.add(check(
                "device-android-inventory",
                "Android Device Inventory",
                inventory.androidAuthorizedDevices() > 0,
                androidRequired
                        ? "At least one authorized Android device"
                        : "Optional authorized Android device",
                inventory.androidDevices()
                        + " detected; "
                        + inventory.androidAuthorizedDevices()
                        + " authorized",
                androidRequired || anyRequired,
                "Connect and authorize an Android device or emulator."
        ));

        checks.add(check(
                "device-android-authorization",
                "Android Device Authorization",
                inventory.androidUnauthorizedDevices() == 0,
                "No unauthorized Android devices",
                inventory.androidUnauthorizedDevices()
                        + " unauthorized",
                false,
                "Accept the USB debugging authorization prompt "
                        + "on each connected Android device."
        ));

        checks.add(check(
                "device-appium-runtime",
                "Appium Runtime",
                inventory.appiumAvailable(),
                "Appium is installed and executable",
                inventory.appiumAvailable()
                        ? "Available"
                        : "Unavailable",
                deviceRequired,
                "Install Appium and the required mobile drivers."
        ));

        checks.add(appiumServerCheck(
                inventory,
                deviceRequired
        ));

        checks.add(check(
                "device-ios-simctl",
                "iOS Simulator Tooling",
                inventory.simctlAvailable(),
                iosRequired
                        ? "xcrun simctl is available"
                        : "Optional xcrun simctl availability",
                inventory.simctlAvailable()
                        ? "Available"
                        : "Unavailable",
                iosRequired,
                "Install Xcode command-line tools and Simulator runtimes."
        ));

        checks.add(check(
                "device-ios-inventory",
                "Booted iOS Simulators",
                inventory.iosBootedSimulators() > 0,
                iosRequired
                        ? "At least one booted iOS simulator"
                        : "Optional booted iOS simulator",
                inventory.iosBootedSimulators()
                        + " booted",
                iosRequired || anyRequired,
                "Boot an iOS simulator or connect the required iOS target."
        ));

        checks.add(check(
                "device-mobile-evidence",
                "Mobile Evidence Directory",
                inventory.mobileEvidenceDirectoryAvailable(),
                "Mobile evidence directory is available",
                inventory.mobileEvidenceDirectoryAvailable()
                        ? "Available"
                        : "Missing",
                false,
                "Run the mobile enterprise demo to publish "
                        + "mobile evidence."
        ));

        boolean androidReady = inventory.androidReady();
        boolean iosReady = inventory.iosReady();
        boolean appiumReady = inventory.appiumReady();

        HealthStatus status = determineStatus(
                inventory.executionMode(),
                androidReady,
                iosReady,
                appiumReady,
                inventory.mobileEvidenceDirectoryAvailable()
        );

        int passed = (int) checks.stream()
                .filter(check ->
                        check.status() == DiagnosticStatus.PASS
                                || check.status()
                                == DiagnosticStatus.SKIPPED)
                .count();

        List<String> actions = checks.stream()
                .filter(check ->
                        check.status() == DiagnosticStatus.FAIL
                                || check.status()
                                == DiagnosticStatus.WARN)
                .map(DiagnosticCheck::correctiveAction)
                .filter(value -> !value.isBlank())
                .distinct()
                .toList();

        Map<String, Object> metadata =
                new LinkedHashMap<>(inventory.metadata());

        metadata.put(
                "executionMode",
                inventory.executionMode()
        );
        metadata.put(
                "androidDevices",
                inventory.androidDevices()
        );
        metadata.put(
                "androidAuthorizedDevices",
                inventory.androidAuthorizedDevices()
        );
        metadata.put(
                "iosBootedSimulators",
                inventory.iosBootedSimulators()
        );
        metadata.put("androidReady", androidReady);
        metadata.put("iosReady", iosReady);
        metadata.put("appiumReady", appiumReady);
        metadata.put(
                "operatingSystem",
                inventory.operatingSystem()
        );

        return new HealthProbeResult(
                definition.id(),
                definition.name(),
                definition.version(),
                definition.severity(),
                status,
                inventory.durationMillis(),
                summary(status, passed, checks.size()),
                inventory.diagnosis(),
                actions,
                checks,
                inventory.evidenceReferences(),
                metadata
        );
    }

    private DiagnosticCheck appiumServerCheck(
            DeviceInventoryResult inventory,
            boolean critical
    ) {
        if (!inventory.appiumServerCheckEnabled()) {
            return new DiagnosticCheck(
                    "device-appium-server",
                    "Appium Server",
                    DiagnosticStatus.SKIPPED,
                    "Optional Appium server reachability",
                    "Disabled",
                    "Appium server reachability was not enabled.",
                    "Set "
                            + "-Dmapaf.device.appium.server.check.enabled=true "
                            + "to validate the live Appium server.",
                    Map.of("critical", false)
            );
        }

        return check(
                "device-appium-server",
                "Appium Server",
                inventory.appiumServerReachable(),
                "Appium status endpoint is reachable",
                inventory.appiumServerReachable()
                        ? inventory.appiumEndpoint()
                        : "Unreachable: "
                        + inventory.appiumEndpoint(),
                critical,
                "Start Appium or configure the correct Appium URL."
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
                        ? "Device diagnostic passed."
                        : "Device diagnostic did not satisfy "
                        + "the configured execution mode.",
                passed
                        ? "No corrective action required."
                        : action,
                Map.of("critical", critical)
        );
    }

    private HealthStatus determineStatus(
            String executionMode,
            boolean androidReady,
            boolean iosReady,
            boolean appiumReady,
            boolean evidenceAvailable
    ) {
        return switch (executionMode) {
            case "ANDROID_REQUIRED" ->
                    androidReady && appiumReady
                            ? evidenceAvailable
                            ? HealthStatus.HEALTHY
                            : HealthStatus.DEGRADED
                            : HealthStatus.UNHEALTHY;

            case "IOS_REQUIRED" ->
                    iosReady && appiumReady
                            ? evidenceAvailable
                            ? HealthStatus.HEALTHY
                            : HealthStatus.DEGRADED
                            : HealthStatus.UNHEALTHY;

            case "ANY_DEVICE_REQUIRED" ->
                    (androidReady || iosReady)
                            && appiumReady
                            ? evidenceAvailable
                            ? HealthStatus.HEALTHY
                            : HealthStatus.DEGRADED
                            : HealthStatus.UNHEALTHY;

            default -> {
                if ((androidReady || iosReady)
                        && appiumReady
                        && evidenceAvailable) {
                    yield HealthStatus.HEALTHY;
                }

                if (androidReady
                        || iosReady
                        || appiumReady
                        || evidenceAvailable) {
                    yield HealthStatus.DEGRADED;
                }

                yield HealthStatus.NOT_CONFIGURED;
            }
        };
    }

    private String summary(
            HealthStatus status,
            int passed,
            int total
    ) {
        return switch (status) {
            case HEALTHY ->
                    "Mobile device automation is fully operational.";
            case DEGRADED ->
                    "Mobile automation is partially available or "
                            + "requires additional runtime preparation.";
            case UNHEALTHY ->
                    "The required mobile execution target is unavailable.";
            case NOT_CONFIGURED ->
                    "Mobile device automation is not configured.";
        } + " " + passed + "/" + total + " checks passed.";
    }
}
