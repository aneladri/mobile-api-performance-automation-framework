# Lab 06 — Performance Testing with k6 and JMeter

## Objective

Run equivalent smoke and load demonstrations against the same mock API and interpret both client-side and target-side metrics.

## Part A — Setup

Portable mode:

```bash
export JMETER_HOME="$HOME/Tools/apache-jmeter-5.6.3"
export PATH="$JMETER_HOME/bin:$PATH"
bash performance/scripts/start-mock-api.sh
curl http://localhost:8089/health
```

Docker mode alternative:

```bash
./gradlew performanceDemoSmokeDocker
```

## Part B — k6 smoke

```bash
curl -X POST http://localhost:8089/admin/reset
./gradlew k6Smoke
```

Record checks, error rate, request rate, average, p90, and p95.

## Part C — JMeter smoke

```bash
curl -X POST http://localhost:8089/admin/reset
./gradlew jmeterSmoke -PjmeterHome="$JMETER_HOME"
open performance/jmeter/reports/smoke/index.html
```

Record APDEX, error percentage, throughput, median, p90, p95, and p99.

## Part D — Load profiles

```bash
curl -X POST http://localhost:8089/admin/reset
./gradlew k6Load
curl -X POST http://localhost:8089/admin/reset
./gradlew jmeterLoad -PjmeterHome="$JMETER_HOME"
```

## Part E — Fault injection

```bash
curl "http://localhost:8089/health?delayMs=750"
curl "http://localhost:8089/api/users/1?errorRate=0.20"
```

Explain expected threshold or SLA failures before rerunning.

## Part F — Compare fairly

Confirm both tools use:

- the same endpoint sequence;
- comparable virtual users/threads;
- comparable duration/iterations;
- the same target state;
- equivalent assertions and SLAs.

## Extension exercise

Add one new endpoint to the mock API, then add matching k6 and JMeter coverage and document the result location.

## Troubleshooting

```bash
bash performance/scripts/resolve-jmeter.sh
curl http://localhost:8089/health
rm -rf performance/jmeter/reports/smoke
rm -f performance/jmeter/results/smoke.jtl
```

## Completion criteria

- k6 smoke succeeds.
- JMeter smoke succeeds.
- JMeter dashboard opens.
- Participant explains p95, throughput, error rate, APDEX, and tool-selection trade-offs.

## Lab extension — generate both HTML reports

1. Run the k6 smoke profile:

   ```bash
   ./gradlew k6Smoke
   ```

2. Confirm both k6 outputs:

   ```bash
   ls -l performance/results/k6/smoke-summary.json
   ls -l performance/k6/reports/smoke/index.html
   ```

3. Run the JMeter smoke profile:

   ```bash
   ./gradlew jmeterSmoke -PjmeterHome="$HOME/Tools/apache-jmeter-5.6.3"
   ```

4. Generate the common dashboard:

   ```bash
   ./gradlew performanceReport
   ```

5. Open all reports:

   ```bash
   open performance/k6/reports/smoke/index.html
   open performance/jmeter/reports/smoke/index.html
   open performance/reports/index.html
   ```

6. Record the request count, throughput, average response time, P95, and error rate reported by each tool. Explain why values can differ when the workload model is not identical.
