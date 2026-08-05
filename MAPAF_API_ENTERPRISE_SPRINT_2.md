# MAPAF Enterprise API Demo - Sprint 2

## Existing demo preserved

```bash
./gradlew apiFrameworkDemo
```

## New enterprise demo

```bash
./gradlew apiEnterpriseDemo
```

The enterprise demo adds:

- execution and correlation IDs
- business-oriented request/response logs
- masked API keys and authorization headers
- response timing and payload-size metrics
- P50, P90, P95 and P99 response statistics
- Allure request, response and metric attachments
- dashboard-ready summary at `api/reports/enterprise-summary.json`

## Validate

```bash
./gradlew clean compileTestJava
./gradlew apiEnterpriseDemo
```

## Allure

```bash
allure serve build/allure-results
```

## Note

The delivery environment could not download Gradle 9.5.1 because outbound access to `services.gradle.org` was unavailable. Run the validation commands above in the normal MAPAF development environment where the Gradle distribution and dependencies are already available.
