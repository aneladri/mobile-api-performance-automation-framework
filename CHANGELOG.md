# Changelog

## v2.7.5 - Business Transaction Reporting

- Added a shared business transaction reporting contract.
- Published transaction breakdowns for Mobile, Portal, and API enterprise summaries.
- Added regression tests for passed and failed transaction mapping.
- Preserved compatibility with the existing performance transaction dashboard.


## 2.7.2 - Multi-Page Engineering Dashboard

- Added dedicated RoomScan Mobile, Portal, API, and Performance report pages.
- Added detailed execution timelines, module statistics, diagnostics, and evidence navigation.
- Fixed HTML generation failure caused by Formatter interpreting CSS percent values.
# Changelog

## 1.0 — Unified k6 and JMeter HTML reporting

- Added dependency-free k6 HTML report generation from native summary exports.
- Added tool-specific k6 report folders for smoke, load, stress, spike, and soak profiles.
- Added a unified MAPAF performance dashboard linking available k6 and JMeter reports.
- Added `performanceReport` and `openPerformanceReport` Gradle tasks.
- Updated k6 and JMeter demo, setup, training, and lab documentation.
- Preserved k6 exit codes so threshold failures still fail Gradle while producing an HTML report.

## 0.9 — JMeter setup, training, and demo documentation

- Updated root setup and quick-start guidance for Portable and Docker modes.
- Added company-managed Mac guidance that avoids Homebrew ownership changes.
- Added exact JMeter 5.6.3 extraction, permission, environment, resolver, and Gradle commands.
- Expanded demo preparation with day-before rehearsal, live sequence, cleanup, fallback, and troubleshooting.
- Updated training plan, trainer guide, prerequisites, project setup, performance lab, demo lab, and capstone.
- Updated k6/JMeter, JMeter, Docker, and mock API guides.
- Aligned all documented report locations and demo commands.

## v1.1.0 - Unified Performance Dashboard

- Added consolidated k6 and JMeter performance dashboard.
- Added automatic parsing of k6 JSON summaries and JMeter JTL files.
- Added side-by-side profile comparison and overall status.
- Added Gradle tasks `performanceDashboardSmoke` and `performanceDashboardLoad`.
- Added dashboard setup and usage documentation.

## v1.2.0 - Smart Dashboard Launcher

- Added `frameworkDashboard` for one-command generate, serve, and open behavior.
- Added automatic port selection and healthy-server reuse.
- Added background server state and targeted shutdown.
- Added HTTP-based report navigation to avoid Safari `file://` restrictions.

## v2.3 - Common Enterprise Runtime
- Common execution identity, evidence, quality gates and masking.
- Shared summary adapters for API and Performance enterprise demos.
- AI-agent governance defaults and regression tests.

## v2.3.1 - Experience Engineering
- Added unified RoomScan story, personas, business transactions, production metrics, and executive outcomes.
- Added experienceEngineeringTest and roomScanStoryDemo Gradle tasks.

## Architecture Foundation Sprint 0.1
- Established MAPAF Enterprise Architecture Blueprint v1.0.
- Added bounded-context and capability ownership governance.
- Added ADR-007 through ADR-011.
- Added architecture validation tooling and automated governance tests.
