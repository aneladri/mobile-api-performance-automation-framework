# MAPAF Enterprise v2.7.2

## Multi-Page RoomScan Engineering Dashboard

### Added

- Dedicated Mobile, Portal, API, and Performance dashboard pages.
- Cross-module navigation and return-to-command-center links.
- Execution statistics, timelines, detailed steps, diagnostics, quality analysis, and evidence links.
- API request/response rendering when execution steps are published.
- Performance engine comparison for k6 and JMeter.
- Backward-compatible empty-state messaging for summaries produced before step-level publishing.

### Fixed

- Removed `String.formatted()` from the large engineering HTML template, resolving `UnknownFormatConversionException` caused by CSS percent characters.

### Generated pages

- `dashboard/reports/index.html`
- `dashboard/reports/mobile.html`
- `dashboard/reports/web.html`
- `dashboard/reports/api.html`
- `dashboard/reports/performance.html`

### Validation commands

```bash
./gradlew clean compileTestJava
./gradlew executiveDashboardUnitTest
./gradlew presentationDemo -PpresentationProfile=FULL -Pheadless=true -PpresentationPacingEnabled=false -PpresentationOpenDashboard=false -PpresentationOpenAllure=false
./gradlew frameworkDashboardDemo
open dashboard/reports/index.html
```
