# MAPAF v2.7.4 Demo Guide

## Production-scale performance

```bash
./gradlew performanceProductionDemo -PtargetRequests=10500
./gradlew frameworkDashboardDemo
open dashboard/reports/performance.html
```

## RoomScan API digital twin

```bash
./gradlew apiRoomScanDigitalTwinDemo
./gradlew frameworkDashboardDemo
open dashboard/reports/api.html
```

The Postman collection is generated at `integrations/postman/RoomScan-Digital-Twin.postman_collection.json`.

## Failure and debugging showcase

```bash
./gradlew failureShowcaseDemo
./gradlew allureShowcase
open dashboard/reports/index.html
```

Each module detail page displays the latest successful execution and the latest expected failure showcase. The failure tests intentionally fail and Gradle is configured with `ignoreFailures=true` so evidence can be generated without stopping the showcase.
