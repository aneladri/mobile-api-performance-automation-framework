# MAPAF Enterprise v2.7.5 — Business Transaction Reporting

## Scope

This increment completes the Business Transaction Breakdown for the Mobile, Portal, and API enterprise execution reports while preserving the existing performance transaction contract.

## Delivered

- Added the shared `BusinessTransaction` reporting record.
- Mobile enterprise summaries now publish one transaction for every recorded workflow step.
- Web enterprise summaries now publish one transaction for every recorded portal workflow step.
- API enterprise summaries now publish network and validation transactions.
- Added unit coverage for passed and failed transaction mapping.
- Preserved backward compatibility with the existing dashboard transaction renderer.

## Expected results

After rerunning the enterprise demos and regenerating the dashboard:

- Mobile report: 7 transactions.
- Portal report: 4 transactions.
- API report: 5 transactions.
- Performance report: existing transaction table remains unchanged.

## Build and validation

```bash
./gradlew clean compileTestJava
./gradlew test --tests core.enterprise.tests.BusinessTransactionTest
./gradlew mobileEnterpriseDemo
./gradlew webEnterpriseDemo
./gradlew apiEnterpriseDemo
./gradlew performanceEnterpriseDemo
./gradlew frameworkDashboardDemo
```
