# MAPAF v2.7.5 Installation

This archive is a complete framework release. Back up local configuration before replacing an existing project, especially `local.properties`, environment files, credentials, and generated reports.

## Build

```bash
chmod +x gradlew
./gradlew clean compileTestJava
```

## Unit validation

```bash
./gradlew test --tests core.enterprise.tests.BusinessTransactionTest
```

## Regenerate module reports

```bash
./gradlew mobileEnterpriseDemo
./gradlew webEnterpriseDemo
./gradlew apiEnterpriseDemo
./gradlew performanceEnterpriseDemo
./gradlew failureShowcaseDemo
```

## Generate Allure and dashboard

```bash
allure generate build/allure-results --clean -o build/allure-report
./gradlew frameworkDashboardDemo
open dashboard/reports/index.html
```
