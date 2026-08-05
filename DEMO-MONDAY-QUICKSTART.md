# MAPAF Enterprise Monday Demo Quick Start

## One-command execution

```bash
cd "$HOME/Library/CloudStorage/OneDrive-PwC/Documents/automation-framework-v2.0"
./gradlew mondayDemo
```

For a rehearsal without opening browser windows:

```bash
OPEN_REPORTS=false HEADLESS=true ./gradlew mondayDemo
```

## Fast path when module reports already exist

```bash
./gradlew failureShowcaseDemo
./gradlew generateAllureEvidence
./gradlew frameworkDashboardDemo
./gradlew validateDemoRelease
open dashboard/reports/index.html
open build/allure-report/index.html
```

## Non-blocking Allure tasks

- `generateAllureEvidence` generates HTML only.
- `openAllureEvidence` opens the generated static report.
- `serveAllureEvidence` starts the Allure server and intentionally blocks until Ctrl+C.
- `allureShowcase` generates and opens the static report without blocking Gradle.

## Readiness gate

```bash
./gradlew demoReadinessGate
```
