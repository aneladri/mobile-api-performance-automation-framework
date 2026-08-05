# MAPAF Enterprise Experience Engineering Demo Guide

## Purpose
Use one RoomScan business narrative across AI Generation, Mobile, API, Performance, Playwright, Dashboard, Allure, and Release Readiness.

## Validate the Story Layer

```bash
./gradlew clean compileTestJava
./gradlew experienceEngineeringTest
./gradlew roomScanStoryDemo
```

## Recommended Presentation Narrative
1. A field technician starts a RoomScan session.
2. The mobile application captures and uploads room images.
3. The backend submits the scan to the AI Gateway/CubiCasa.
4. MAPAF validates polling, completion, and floor-plan retrieval.
5. The RoomScan portal supports review and approval.
6. Performance testing validates 10,000+ production-like requests.
7. The dashboard combines evidence and provides a governed release recommendation.

## Existing Demos
Existing quick and enterprise commands remain available and unchanged.
