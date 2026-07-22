# MAPAF Unified Performance Dashboard

## Purpose

The dashboard provides one entry point for k6 and JMeter performance results while preserving each tool's detailed HTML report.

## Generate reports

```bash
./gradlew k6Smoke
./gradlew jmeterSmoke -PjmeterHome="$HOME/Tools/apache-jmeter-5.6.3"
```

## Generate the dashboard

```bash
./gradlew performanceReport
```

## Generate and open it on macOS

```bash
./gradlew openPerformanceReport
```

The dashboard is created at:

```text
performance/reports/index.html
```

## Information shown

- Overall status
- Reports and profiles discovered
- Requests, throughput and response-time metrics
- P95 and maximum response time
- Error rate and successful checks
- Side-by-side k6 and JMeter profile comparison
- Links to tool-specific HTML and raw result files

## Source files

k6 summary files:

```text
performance/results/k6/<profile>-summary.json
```

JMeter result files:

```text
performance/jmeter/results/<profile>.jtl
```

Detailed HTML reports remain under each tool's report folder.
