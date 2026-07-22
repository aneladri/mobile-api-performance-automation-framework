# MAPAF — Modern Automation Platform and Framework

MAPAF is a unified Quality Engineering framework for API, mobile, performance, AI-assisted analysis, locator discovery, reporting, and CI/CD enablement.

## Supported capabilities

| Capability | Technology | Status |
|---|---|---|
| API automation | Java, TestNG, REST Assured | Available |
| Functional API mock | Embedded WireMock | Available |
| Android/iOS automation | Appium 2 | Available |
| Performance testing | k6 and Apache JMeter | Available |
| Performance demo target | Standalone Node.js mock API | Available |
| Reporting | Allure, k6 summaries, JMeter HTML dashboard | Available |
| AI code generation | Configurable generation workflow | Available |
| Verified mobile locator discovery | Appium hierarchy parsing | Initial implementation |
| Web automation | Playwright Java | Planned |

## Design overview

```text
Functional API tests --> Embedded WireMock (random port, TestNG lifecycle)
Performance tests    --> Standalone mock API (port 8089)
                           |                     |
                           +--> k6              +--> JMeter

All test types --> common configuration, logging, reports, and CI/CD
```

## Repository structure

```text
src/test/java/                  Java framework and tests
src/test/resources/             Payloads, schemas, prompts, configuration
performance/mock-api/           Long-running performance/demo API
performance/k6/                 k6 smoke/load/stress/spike/soak scripts
performance/jmeter/             JMX plans, CSV data, JTL and HTML reports
performance/scripts/            Portable and Docker runners
docs/training/framework-labs/   Step-by-step labs
```

## Prerequisites

Required for all users:

- Git
- Java 17
- Gradle wrapper included in the repository

Capability-specific tools:

| Capability | Requirement |
|---|---|
| API | No extra tool; WireMock is embedded |
| Performance mock API | Node.js 18+ in portable mode, or Docker |
| k6 | k6 on `PATH`, or Docker mode |
| JMeter | Portable Apache JMeter 5.6.3, or Docker mode |
| Mobile | Node.js, Appium 2, Android/iOS tools |

## JMeter setup on a managed corporate Mac

Do **not** change ownership of a company-managed Homebrew installation. Use the portable binary archive.

### 1. Create a user-owned tools directory

```bash
mkdir -p "$HOME/Tools"
```

### 2. Download and extract Apache JMeter 5.6.3

Download `apache-jmeter-5.6.3.tgz` from the approved Apache distribution location, then run:

```bash
tar -xzf "$HOME/Downloads/apache-jmeter-5.6.3.tgz" -C "$HOME/Tools"
chmod +x "$HOME/Tools/apache-jmeter-5.6.3/bin/jmeter"
```

### 3. Configure the current terminal

```bash
export JMETER_HOME="$HOME/Tools/apache-jmeter-5.6.3"
export PATH="$JMETER_HOME/bin:$PATH"
```

Verify:

```bash
jmeter --version
bash performance/scripts/resolve-jmeter.sh
```

Persist the configuration when permitted:

```bash
echo 'export JMETER_HOME="$HOME/Tools/apache-jmeter-5.6.3"' >> ~/.zshrc
echo 'export PATH="$JMETER_HOME/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

## Quick start

```bash
./gradlew clean compileTestJava
./gradlew apiTest
```

### Portable performance mode

Terminal 1:

```bash
bash performance/scripts/start-mock-api.sh
```

Terminal 2:

```bash
curl http://localhost:8089/health
./gradlew k6Smoke
./gradlew jmeterSmoke -PjmeterHome="$HOME/Tools/apache-jmeter-5.6.3"
```

Open the JMeter dashboard:

```bash
open performance/jmeter/reports/smoke/index.html
```

Combined smoke demo:

```bash
./gradlew performanceDemoSmoke \
  -PjmeterHome="$HOME/Tools/apache-jmeter-5.6.3"
```

### Docker performance mode

```bash
./gradlew performanceDemoSmokeDocker
```

No local Node.js, k6, or JMeter installation is required for the combined Docker demo.

## Extension guide

- Add k6 profiles under `performance/k6/<profile>/`.
- Add JMeter plans under `performance/jmeter/plans/` and keep host, port, protocol, threads, loops, and ramp-up parameterized.
- Add deterministic mock endpoints under `performance/mock-api/server.js`.
- Add new Gradle tasks only after the direct shell command works.
- Update the relevant lab, setup guide, and demo guide with every capability change.

## Troubleshooting

### JMeter not found

```bash
bash performance/scripts/resolve-jmeter.sh
ls -l "$HOME/Tools/apache-jmeter-5.6.3/bin/jmeter"
chmod +x "$HOME/Tools/apache-jmeter-5.6.3/bin/jmeter"
```

Run with an explicit path:

```bash
./gradlew jmeterSmoke \
  -PjmeterHome="$HOME/Tools/apache-jmeter-5.6.3"
```

### Port 8089 is occupied

```bash
lsof -i :8089
bash performance/scripts/stop-mock-api.sh
```

### JMeter report already exists

```bash
rm -rf performance/jmeter/reports/smoke
rm -f performance/jmeter/results/smoke.jtl
```

## Best practices

- Run JMeter in non-GUI mode for load generation.
- Use GUI only to design or inspect a plan.
- Run smoke before load, stress, spike, or soak.
- Reset mock API state between comparison runs.
- Keep workloads equivalent when comparing k6 and JMeter.
- Use portable mode on managed laptops and Docker mode in CI/shared demos.
- Never treat laptop demo results as production capacity baselines.

## Detailed guides

- [Start here](docs/START_HERE.md)
- [Training guide](docs/TRAINING.md)
- [Demo preparation](DEMO_PREP_README.md)
- [k6 and JMeter demo guide](docs/performance/K6_JMETER_DEMO_GUIDE.md)
- [JMeter guide](performance/jmeter/README.md)
- [Training labs](docs/training/framework-labs/README.md)

## Unified k6 and JMeter HTML reporting

Both performance engines now generate browser-ready HTML reports.

### k6 HTML report

Run:

```bash
./gradlew k6Smoke
```

Open:

```bash
open performance/k6/reports/smoke/index.html
```

The k6 runner retains the machine-readable summary at:

```text
performance/results/k6/smoke-summary.json
```

and converts it into a dependency-free MAPAF HTML report containing request volume, request rate, response-time percentiles, failed-request rate, thresholds, checks, virtual users, iterations, and data transfer.

### JMeter HTML report

Run:

```bash
./gradlew jmeterSmoke -PjmeterHome="$HOME/Tools/apache-jmeter-5.6.3"
```

Open:

```bash
open performance/jmeter/reports/smoke/index.html
```

### Unified performance dashboard

Generate a common entry page for every available k6 and JMeter report:

```bash
./gradlew performanceReport
```

Open it directly on macOS:

```bash
./gradlew openPerformanceReport
```

Report location:

```text
performance/reports/index.html
```

The dashboard links to all generated smoke, load, stress, spike, and soak reports that are available for either tool.

## Unified Performance Dashboard

After running k6 and JMeter, generate a single dashboard:

```bash
./gradlew performanceReport
```

Generate and open it on macOS:

```bash
./gradlew openPerformanceReport
```

The entry point is:

```text
performance/reports/index.html
```

It summarizes k6 JSON data and JMeter JTL results, compares tools by profile, and links to each detailed HTML report. See `docs/performance/UNIFIED_PERFORMANCE_DASHBOARD.md`.

## Smart performance dashboard launcher

Generate, serve, and open the unified k6 + JMeter dashboard with one command:

```bash
./gradlew frameworkDashboard
```

The launcher:

1. regenerates `performance/reports/index.html`;
2. reuses the existing MAPAF report server when it is healthy;
3. otherwise selects the first available port from `8090` through `8110`;
4. starts the server in the background;
5. opens the dashboard over HTTP to avoid Safari `file://` restrictions.

Optional commands:

```bash
./gradlew servePerformanceReport
./gradlew openPerformanceReportHttp
./gradlew stopPerformanceReportServer
```

Choose a preferred port or search range:

```bash
./gradlew frameworkDashboard \
  -PperformanceReportPort=8091 \
  -PperformanceReportMaxPort=8120
```

## Unified Quality Engineering Dashboard

MAPAF now provides one dashboard for API functional and performance testing, with reserved sections for Web, Mobile, and AI insights.

Generate and open the dashboard from existing results:

```bash
./gradlew frameworkDashboard
```

Run the API suite and then open the refreshed dashboard:

```bash
./gradlew qualityDashboard
```

Generate the dashboard without opening a browser:

```bash
./gradlew qualityReport
```

Dashboard navigation:

```text
Overview | API | Performance | Web | Mobile | AI Insights | Environment | Downloads
```
