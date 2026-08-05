# MAPAF Performance Demo Preparation

This guide prepares and delivers the functional API, k6, and JMeter demo in either **Portable mode** or **Docker mode**.

## 1. Choose the execution mode

| Mode | Best for | Requirements |
|---|---|---|
| Portable | PwC/company-managed Mac | Java 17, Node.js, k6, portable JMeter 5.6.3 |
| Docker | Repeatable team demos and CI/CD | Approved Docker runtime |

Use Portable mode when Homebrew is managed or not writable. Do not change ownership of `/opt/homebrew` on a managed machine.

## 2. Validate the framework

```bash
cd ~/Documents/automation-framework-updated
./gradlew clean compileTestJava
./gradlew apiTest
```

Expected: `BUILD SUCCESSFUL` and all API tests pass.

## 3. Portable mode — one-time JMeter setup

```bash
mkdir -p "$HOME/Tools"
tar -xzf "$HOME/Downloads/apache-jmeter-5.6.3.tgz" -C "$HOME/Tools"
chmod +x "$HOME/Tools/apache-jmeter-5.6.3/bin/jmeter"
export JMETER_HOME="$HOME/Tools/apache-jmeter-5.6.3"
export PATH="$JMETER_HOME/bin:$PATH"
```

Verify:

```bash
jmeter --version
bash performance/scripts/resolve-jmeter.sh
"$(bash performance/scripts/resolve-jmeter.sh)" --version
```

## 4. Portable mode — day-before rehearsal

Terminal 1:

```bash
bash performance/scripts/start-mock-api.sh
```

Terminal 2:

```bash
curl http://localhost:8089/health
curl -X POST http://localhost:8089/admin/reset
./gradlew k6Smoke
./gradlew jmeterSmoke -PjmeterHome="$JMETER_HOME"
```

Verify artifacts:

```bash
ls -l performance/results/k6/smoke-summary.json
ls -l performance/jmeter/results/smoke.jtl
ls -l performance/jmeter/reports/smoke/index.html
```

Open the report:

```bash
open performance/jmeter/reports/smoke/index.html
```

## 5. Docker mode rehearsal

```bash
docker version
docker compose version
./gradlew performanceDemoSmokeDocker
```

Docker mode starts the mock API, runs k6 and JMeter containers, writes reports to the host, and stops services after completion.

## 6. Live demo sequence

### Step 1 — Explain the architecture

```text
API functional suite --> embedded WireMock
k6/JMeter suite      --> standalone mock API on port 8089
```

### Step 2 — Run functional API automation

```bash
./gradlew apiTest
```

Show positive, authentication, schema, and expected error-path coverage.

### Step 3 — Start and inspect the performance target

```bash
bash performance/scripts/start-mock-api.sh
curl http://localhost:8089/health
curl http://localhost:8089/metrics
curl "http://localhost:8089/health?delayMs=500"
curl -i http://localhost:8089/status/500
```

### Step 4 — Run k6 smoke

```bash
curl -X POST http://localhost:8089/admin/reset
./gradlew k6Smoke
```

Explain checks, request rate, failed requests, average latency, p90/p95, and thresholds.

### Step 5 — Run JMeter smoke

```bash
curl -X POST http://localhost:8089/admin/reset
./gradlew jmeterSmoke -PjmeterHome="$JMETER_HOME"
open performance/jmeter/reports/smoke/index.html
```

Show APDEX, percentiles, throughput, error percentage, active threads, and transactions per second.

### Step 6 — Compare the tools

| Area | k6 | JMeter |
|---|---|---|
| Authoring | JavaScript | JMX/GUI |
| Version control | Strong | Moderate |
| CI/CD | Strong | Strong in non-GUI mode |
| Resource use | Lightweight | Heavier |
| Best fit | Developer-centric teams | Enterprise QA and protocol-rich needs |

## 7. Combined commands

Portable:

```bash
./gradlew performanceDemoSmoke -PjmeterHome="$JMETER_HOME"
```

Docker:

```bash
./gradlew performanceDemoSmokeDocker
```

## 8. Reset previous artifacts before the demo

```bash
bash performance/scripts/stop-mock-api.sh || true
rm -f performance/jmeter/results/*.jtl
rm -rf performance/jmeter/reports/smoke performance/jmeter/reports/load
rm -f performance/results/k6/*-summary.json
rm -f performance/reports/mock-api/metrics-after-*.json
```

## 9. Troubleshooting

### JMeter not found

```bash
echo "$JMETER_HOME"
bash performance/scripts/resolve-jmeter.sh
ls -l "$HOME/Tools/apache-jmeter-5.6.3/bin/jmeter"
```

Explicit path:

```bash
./gradlew jmeterSmoke \
  -PjmeterHome="$HOME/Tools/apache-jmeter-5.6.3"
```

### Mock API does not start

```bash
lsof -i :8089
cat performance/reports/mock-api/mock-api.log
bash performance/scripts/stop-mock-api.sh
```

### Dashboard generation fails

```bash
rm -rf performance/jmeter/reports/smoke
rm -f performance/jmeter/results/smoke.jtl
./gradlew jmeterSmoke -PjmeterHome="$JMETER_HOME"
```

## 10. Presenter fallback plan

- Keep one previous JMeter HTML dashboard open.
- Keep one k6 summary file available.
- If JMeter fails, run k6 and show the saved JMeter dashboard.
- If k6 fails, run JMeter and show the saved k6 summary.
- If local tooling fails and Docker is approved, switch to `performanceDemoSmokeDocker`.

## 11. Final presenter checklist

- [ ] Notifications disabled
- [ ] Terminal font enlarged
- [ ] No credentials visible
- [ ] Framework compiles
- [ ] API suite passes
- [ ] Mock API health returns 200
- [ ] k6 smoke passes
- [ ] JMeter smoke passes
- [ ] HTML dashboard opens
- [ ] Fallback reports are ready

## k6 and JMeter HTML report walkthrough

After the smoke tests complete, open both tool-specific reports and the unified dashboard:

```bash
open performance/k6/reports/smoke/index.html
open performance/jmeter/reports/smoke/index.html
./gradlew openPerformanceReport
```

During the demo, explain:

1. k6 produces a JSON summary and a MAPAF HTML report.
2. JMeter produces a JTL file and its standard HTML dashboard.
3. `performance/reports/index.html` provides one entry point for both engines.
4. k6 and JMeter remain independently executable, while reporting is presented consistently.

### Recommended presenter sequence

```bash
./gradlew k6Smoke
./gradlew jmeterSmoke -PjmeterHome="$HOME/Tools/apache-jmeter-5.6.3"
./gradlew openPerformanceReport
```

Use the unified dashboard to move between the reports rather than navigating through folders during the presentation.

## Open the unified performance dashboard

After running the k6 and JMeter demos, use:

```bash
./gradlew frameworkDashboard
```

This command avoids Safari local-file restrictions, reuses an existing healthy MAPAF report server, and automatically selects another port when `8090` is occupied.

To stop the managed server after the demo:

```bash
./gradlew stopPerformanceReportServer
```
