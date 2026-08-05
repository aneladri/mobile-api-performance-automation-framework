# ADR-003: Additive Mobile Enterprise Experience

## Decision
Keep the existing `mobileFrameworkDemo` unchanged and add `mobileEnterpriseDemo` as an additive RoomScan business demonstration.

## Rationale
The framework already contains Appium lifecycle management, RoomScan workflow state, capture abstractions, healing telemetry, screenshots, and Allure support. The enterprise layer should present those capabilities through a coherent business workflow rather than replace the underlying framework.

## Consequences
- Existing quick demos remain backward compatible.
- The enterprise demo uses `MockCaptureProvider` until the real RoomScan application exposes device-controlled capture APIs.
- Presentation pacing is enabled only for live demos and can be disabled in CI.
- Dashboard output is published to `mobile/reports/enterprise-summary.json`.
