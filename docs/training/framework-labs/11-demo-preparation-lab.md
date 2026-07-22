# Lab 11 — Performance Demo Preparation

## Objective

Prepare a reliable 20–25 minute API, k6, and JMeter demonstration with fallback assets.

## Step 1 — Validate tools

```bash
java -version
node --version
k6 version
jmeter --version
bash performance/scripts/resolve-jmeter.sh
```

## Step 2 — Validate framework and API

```bash
./gradlew clean compileTestJava
./gradlew apiTest
```

## Step 3 — Start the performance target

```bash
bash performance/scripts/start-mock-api.sh
curl http://localhost:8089/health
```

## Step 4 — Generate fallback artifacts

```bash
./gradlew k6Smoke
./gradlew jmeterSmoke -PjmeterHome="$HOME/Tools/apache-jmeter-5.6.3"
open performance/jmeter/reports/smoke/index.html
```

## Step 5 — Rehearse presenter sequence

1. Architecture overview.
2. Functional API suite.
3. Mock API health, metrics, delay, and error simulation.
4. k6 smoke output.
5. JMeter smoke output and HTML dashboard.
6. Tool comparison and roadmap.

## Step 6 — Cleanup before live execution

```bash
rm -f performance/jmeter/results/*.jtl
rm -rf performance/jmeter/reports/smoke performance/jmeter/reports/load
rm -f performance/results/k6/*-summary.json
curl -X POST http://localhost:8089/admin/reset
```

## Step 7 — Fallback plan

- Keep previous k6 summary and JMeter dashboard.
- Be ready to switch to Docker mode.
- Use smoke profiles for the live run.
- Avoid a deliberate failure in the primary sequence.

## Completion criteria

- All live commands are rehearsed.
- Reports open without editing paths.
- Fallback reports exist.
- Presenter can explain every metric shown.
