# MAPAF v2.9 Sprint 3.3.2

## Device Health Probe

### Delivered

- Android Debug Bridge validation
- Android connected-device inventory
- Android authorization validation
- Appium runtime validation
- Optional Appium server reachability
- Xcode simctl validation
- Booted iOS simulator inventory
- Device execution-mode policies
- Mobile evidence-directory validation
- Device Health unit tests
- MAPAF Doctor publisher integration

### Execution modes

- `OPTIONAL`
- `ANDROID_REQUIRED`
- `IOS_REQUIRED`
- `ANY_DEVICE_REQUIRED`

The execution mode can be configured using:

- `MAPAF_DEVICE_EXECUTION_MODE`
- `-Dmapaf.device.execution.mode`

### Status behavior

- Required target ready with Appium and evidence: `HEALTHY`
- Optional partial mobile capability: `DEGRADED`
- Required execution target unavailable: `UNHEALTHY`
- No optional mobile capability configured: `NOT_CONFIGURED`

### Appium server validation

Enable live server validation with:

`-Dmapaf.device.appium.server.check.enabled=true`

Override the endpoint with:

`-Dmapaf.device.appium.url=http://127.0.0.1:4723/status`
