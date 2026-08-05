# MAPAF v2.4 Playwright Enterprise Demo Guide

## Demo objective
Validate the RoomScan quality portal from authentication through AI floor-plan review, approval and submission.

## Run
```bash
./gradlew webEnterpriseDemo -Pbrowser=CHROMIUM -Pheadless=false
```

## Evidence
- `web/artifacts/` screenshots, trace, video, console and page-error logs
- `web/reports/enterprise-summary.json`
- `build/allure-results/`

## Presentation narrative
1. Quality Engineer signs in.
2. Searches for completed scan RS-1045.
3. Reviews CubiCasa-generated floor plan and metadata.
4. Approves and submits the scan.
5. MAPAF correlates UI actions with backend API responses and publishes evidence.
