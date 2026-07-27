# MAPAF Training Guide

## Learning objectives

Participants will be able to:

- compile and run the framework;
- execute functional API tests with embedded WireMock;
- run the standalone performance mock API;
- execute and interpret k6 and JMeter tests;
- choose Portable or Docker execution mode;
- troubleshoot tool-resolution and report-generation failures;
- extend test plans, scripts, and mock endpoints safely.

## Recommended learning path

| Day | Modules |
|---|---|
| 1 | Setup, architecture, API baseline |
| 2 | API payloads, schemas, authentication, mocks |
| 3 | Mobile architecture and execution |
| 4 | Performance testing with k6 and JMeter |
| 5 | AI analysis, locator discovery, debugging |
| 6 | Demo preparation and capstone |

## JMeter setup for managed Macs

Do not change Homebrew ownership. Install JMeter under the user home directory.

```bash
mkdir -p "$HOME/Tools"
tar -xzf "$HOME/Downloads/apache-jmeter-5.6.3.tgz" -C "$HOME/Tools"
chmod +x "$HOME/Tools/apache-jmeter-5.6.3/bin/jmeter"
export JMETER_HOME="$HOME/Tools/apache-jmeter-5.6.3"
export PATH="$JMETER_HOME/bin:$PATH"
```

Validate:

```bash
jmeter --version
bash performance/scripts/resolve-jmeter.sh
```

## Trainer demonstration sequence

1. Run `./gradlew apiTest`.
2. Explain embedded WireMock versus the standalone performance target.
3. Start `bash performance/scripts/start-mock-api.sh`.
4. Run `./gradlew k6Smoke`.
5. Run `./gradlew jmeterSmoke -PjmeterHome="$JMETER_HOME"`.
6. Open `performance/jmeter/reports/smoke/index.html`.
7. Compare k6 and JMeter using equivalent workloads.
8. Show latency/error injection and rerun a smoke profile.

## Extension guide

Every new performance scenario should include:

- purpose and workload model;
- setup prerequisites;
- direct execution command;
- Gradle wrapper command;
- report location;
- threshold/SLA definition;
- cleanup and reset steps;
- troubleshooting notes;
- Portable and Docker considerations.

## Troubleshooting method

1. Validate the tool independently (`jmeter --version`, `k6 version`).
2. Validate the resolver (`resolve-jmeter.sh`).
3. Validate the target (`curl /health`).
4. Run the shell script directly.
5. Run the Gradle wrapper task.
6. Inspect JTL, dashboard, logs, and target metrics.

## Best practices

- Use non-GUI JMeter for execution.
- Keep training workloads small and deterministic.
- Use synthetic data only.
- Reset mock API state before comparisons.
- Do not present demo results as production capacity evidence.
- Archive reports for training fallback.

See [framework labs](training/framework-labs/README.md).

## Performance reporting exercise

Learners must execute both smoke profiles and review the two report formats.

```bash
./gradlew k6Smoke
./gradlew jmeterSmoke -PjmeterHome="$HOME/Tools/apache-jmeter-5.6.3"
./gradlew performanceReport
```

Required review points:

- k6 request count, rate, average, P90, P95, error rate, thresholds, and checks;
- JMeter APDEX, response-time percentiles, throughput, active threads, and errors;
- differences between the tool-native reports;
- use of `performance/reports/index.html` as the unified navigation page.
