package dashboard.enterprise.doctor.probe.device;

import java.util.List;
import java.util.Map;

public record DeviceInventoryResult(
        String executionMode,
        boolean adbAvailable,
        int androidDevices,
        int androidAuthorizedDevices,
        int androidUnauthorizedDevices,
        boolean appiumAvailable,
        boolean appiumServerCheckEnabled,
        boolean appiumServerReachable,
        String appiumEndpoint,
        boolean simctlAvailable,
        int iosBootedSimulators,
        boolean mobileEvidenceDirectoryAvailable,
        String operatingSystem,
        String diagnosis,
        long durationMillis,
        List<String> androidDeviceIds,
        List<String> iosSimulatorIds,
        List<String> evidenceReferences,
        Map<String, Object> metadata
) {

    public DeviceInventoryResult {
        executionMode = safe(executionMode);
        appiumEndpoint = safe(appiumEndpoint);
        operatingSystem = safe(operatingSystem);
        diagnosis = safe(diagnosis);

        androidDeviceIds = androidDeviceIds == null
                ? List.of()
                : List.copyOf(androidDeviceIds);

        iosSimulatorIds = iosSimulatorIds == null
                ? List.of()
                : List.copyOf(iosSimulatorIds);

        evidenceReferences = evidenceReferences == null
                ? List.of()
                : List.copyOf(evidenceReferences);

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);

        if (androidDevices < 0
                || androidAuthorizedDevices < 0
                || androidUnauthorizedDevices < 0
                || iosBootedSimulators < 0
                || durationMillis < 0) {
            throw new IllegalArgumentException(
                    "Device inventory counts and duration cannot be negative."
            );
        }
    }

    public boolean androidReady() {
        return adbAvailable && androidAuthorizedDevices > 0;
    }

    public boolean iosReady() {
        return simctlAvailable && iosBootedSimulators > 0;
    }

    public boolean appiumReady() {
        return appiumAvailable
                && (!appiumServerCheckEnabled
                || appiumServerReachable);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
