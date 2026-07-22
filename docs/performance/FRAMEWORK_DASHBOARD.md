# MAPAF Smart Performance Dashboard Launcher

## Purpose

The launcher provides one reliable entry point for the unified k6 and JMeter dashboard. It serves reports over HTTP rather than `file://`, which avoids Safari local-file navigation errors.

## Start and open

```bash
./gradlew frameworkDashboard
```

## Behavior

- Generates the latest unified dashboard.
- Reuses a healthy MAPAF-managed report server.
- Uses port `8090` by default.
- Tries successive ports through `8110` if the preferred port is occupied.
- Starts Python's HTTP server in the background.
- Opens the dashboard in the default browser on macOS.
- Stores the managed PID, port, and server log under `performance/reports/.server/`.

## Start without opening

```bash
./gradlew servePerformanceReport
```

## Open or reuse

```bash
./gradlew openPerformanceReportHttp
```

## Stop

```bash
./gradlew stopPerformanceReportServer
```

## Custom ports

```bash
./gradlew frameworkDashboard \
  -PperformanceReportPort=8091 \
  -PperformanceReportMaxPort=8120
```

## Troubleshooting

Inspect the managed server state:

```bash
cat performance/reports/.server/port
cat performance/reports/.server/pid
cat performance/reports/.server/server.log
```

If another application owns the preferred port, MAPAF selects the next free port automatically. The stop task only terminates the server recorded by MAPAF.
