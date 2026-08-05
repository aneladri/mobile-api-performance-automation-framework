# MAPAF Enterprise v2.4 Validation

## Included
- Existing `playwrightDemo` remains unchanged.
- New `webEnterpriseDemo` task.
- New `webEnterpriseUnitTest` task.
- RoomScan portal business workflow.
- Playwright API routing and network evidence.
- Step screenshots, trace-on-success, video, console and page-error capture.
- Allure attachments.
- Dashboard summary at `web/reports/enterprise-summary.json`.
- Architecture decision, demo guide and release notes.

## Local validation commands
```bash
./gradlew clean compileTestJava
./gradlew webEnterpriseUnitTest
./gradlew webEnterpriseDemo -Pbrowser=CHROMIUM -Pheadless=false
allure serve build/allure-results
```

## Delivery environment limitation
The delivery environment could not download Gradle 9.5.1 from `services.gradle.org`, so the full Gradle build must be run in the local MAPAF development environment.
