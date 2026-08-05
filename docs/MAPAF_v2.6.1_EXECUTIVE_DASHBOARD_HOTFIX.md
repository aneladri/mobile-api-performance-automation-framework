# MAPAF Enterprise v2.6.1 - Executive Dashboard Hotfix

## Scope

This hotfix replaces the two dashboard aggregation classes that were previously edited incrementally.

## Fixes

- Reads performance status from `qualityGate` in addition to `result`, `status`, and `overallResult`.
- Reads performance duration from `durationSeconds` and converts it to milliseconds.
- Displays Performance as `PASS` when its enterprise quality gate passes.
- Calculates release readiness against all required module cards, including modules that have not run.
- Returns `PARTIAL` and `MEDIUM` risk while any required module result is missing.
- Removes duplicate aggregation variables and avoids unsupported method references.
- Adds regression tests for performance summary mapping and partial readiness.

## Validation commands

```bash
./gradlew clean compileTestJava
./gradlew executiveDashboardUnitTest
./gradlew frameworkDashboardDemo
open dashboard/reports/index.html
```

## Expected result after all four enterprise demos have run

- Mobile: PASS
- Web: PASS
- API: PASS
- Performance: PASS
- Release Readiness: 100%
- Business Risk: LOW
- Overall Status: PASS
- Recommendation: READY FOR PRODUCTION
