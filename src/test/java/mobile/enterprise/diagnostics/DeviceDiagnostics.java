package mobile.enterprise.diagnostics;

import mobile.utils.DeviceInfoUtils;

public record DeviceDiagnostics(
        String platform,
        String platformVersion,
        String deviceName,
        String automationName,
        String sessionId,
        String network,
        String orientation,
        String diagnosticsStatus
) {
    public static DeviceDiagnostics collect() {
        return new DeviceDiagnostics(
                DeviceInfoUtils.getPlatformName(),
                DeviceInfoUtils.getPlatformVersion(),
                DeviceInfoUtils.getDeviceName(),
                DeviceInfoUtils.getAutomationName(),
                DeviceInfoUtils.getSessionId(),
                "WiFi / Local Lab",
                "Portrait",
                "AVAILABLE"
        );
    }
}
