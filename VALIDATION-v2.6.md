# MAPAF v2.6 Validation

Validated in the delivery environment:
- Core executive dashboard model, aggregation and HTML renderer compile with `javac`.
- Gradle task definitions and TestNG suite files are included.

Full Gradle execution could not run because the delivery environment cannot resolve `services.gradle.org`. Run locally:

```bash
./gradlew clean compileTestJava
./gradlew executiveDashboardUnitTest
./gradlew frameworkDashboardDemo
```
