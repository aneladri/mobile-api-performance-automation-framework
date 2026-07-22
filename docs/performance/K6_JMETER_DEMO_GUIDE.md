# k6 and JMeter Performance Demo Guide

## Objective

Demonstrate two performance tools against the same deterministic API target while preserving comparable workloads, reports, and repeatable setup.

## Architecture

```text
Standalone mock API :8089
       |       |
      k6     JMeter
       |       |
 JSON summary  JTL + HTML dashboard
```

## Execution modes

### Portable mode

Best for managed corporate laptops.

```bash
export JMETER_HOME="$HOME/Tools/apache-jmeter-5.6.3"
export PATH="$JMETER_HOME/bin:$PATH"
bash performance/scripts/start-mock-api.sh
./gradlew k6Smoke
./gradlew jmeterSmoke -PjmeterHome="$JMETER_HOME"
```

### Docker mode

Best for shared demos and CI/CD.

```bash
./gradlew performanceDemoSmokeDocker
```

## JMeter portable setup

```bash
mkdir -p "$HOME/Tools"
tar -xzf "$HOME/Downloads/apache-jmeter-5.6.3.tgz" -C "$HOME/Tools"
chmod +x "$HOME/Tools/apache-jmeter-5.6.3/bin/jmeter"
export JMETER_HOME="$HOME/Tools/apache-jmeter-5.6.3"
export PATH="$JMETER_HOME/bin:$PATH"
jmeter --version
```

## Demo steps

1. Reset target metrics.
2. Run k6 smoke.
3. Capture k6 checks, error rate, throughput, and percentiles.
4. Reset target metrics again.
5. Run JMeter smoke.
6. Open the JMeter HTML dashboard.
7. Compare equivalent business transactions and load shape.
8. Show target-side `/metrics`.
9. Optionally inject delay or error rate and rerun.

Commands:

```bash
curl -X POST http://localhost:8089/admin/reset
./gradlew k6Smoke
curl -X POST http://localhost:8089/admin/reset
./gradlew jmeterSmoke -PjmeterHome="$JMETER_HOME"
open performance/jmeter/reports/smoke/index.html
curl http://localhost:8089/metrics
```

## Result locations

```text
performance/results/k6/
performance/jmeter/results/
performance/jmeter/reports/
performance/reports/mock-api/
```

## Extension guide

- Keep the same API sequence in both tools.
- Parameterize environment and load settings.
- Add correlation where IDs are created dynamically.
- Add response, content, and latency assertions.
- Define pass/fail thresholds before execution.
- Add a Docker validation when a scenario will run in CI.

## Troubleshooting

```bash
bash performance/scripts/resolve-jmeter.sh
curl http://localhost:8089/health
rm -rf performance/jmeter/reports/smoke
rm -f performance/jmeter/results/smoke.jtl
```

## Best practices

- Do not run JMeter load tests from GUI mode.
- Run smoke before load.
- Use separate target and load-generator hosts for formal testing.
- Monitor application infrastructure during real tests.
- Do not compare tools unless the workload is equivalent.

## HTML reporting

### k6

Every k6 profile creates:

```text
performance/results/k6/<profile>-summary.json
performance/k6/reports/<profile>/index.html
```

The HTML report is generated without an external npm package. The repository-owned Node.js converter reads the native k6 summary export and renders the MAPAF report.

### JMeter

Every JMeter profile creates:

```text
performance/jmeter/results/<profile>.jtl
performance/jmeter/reports/<profile>/index.html
```

### Common dashboard

Run:

```bash
./gradlew performanceReport
```

or generate and open it on macOS:

```bash
./gradlew openPerformanceReport
```

The common dashboard is written to:

```text
performance/reports/index.html
```

It discovers existing k6 and JMeter reports and displays links only for profiles that have already been executed.
