# MAPAF Enterprise v2.7.4 Release Validation

## Packaging checks completed

- Baseline: MAPAF Enterprise v2.7.3 full release.
- New Java/XML/Gradle files added with balanced delimiter and archive integrity checks.
- Existing demo tasks preserved.
- Allure categories expanded for product, API, performance, locator/healing, external AI provider and environment failures.

## Local validation required

The packaging environment could not resolve `services.gradle.org`, so run locally:

```bash
./gradlew clean compileTestJava
./gradlew apiRoomScanDigitalTwinDemo
./gradlew performanceProductionDemo -PtargetRequests=10500
./gradlew failureShowcaseDemo
./gradlew frameworkDashboardDemo
./gradlew allureShowcase
```

The failure tasks intentionally contain failing TestNG tests and use `ignoreFailures=true` to preserve evidence generation.
