# MAPAF v2.6 Executive Dashboard Demo Guide

## Generate the command center

```bash
./gradlew frameworkDashboardDemo
```

## Open it

```bash
open dashboard/reports/index.html
```

## Expected inputs

- `mobile/reports/enterprise-summary.json`
- `web/reports/enterprise-summary.json`
- `api/reports/enterprise-summary.json`
- `performance/reports/enterprise-summary.json`

Missing inputs are shown as `NOT_RUN`; dashboard generation still succeeds.

## Presentation flow

1. Start with Executive View.
2. Explain readiness, risk and recommendation.
3. Switch to Engineering View.
4. Open each module evidence link.
5. Close with the executive narrative.
