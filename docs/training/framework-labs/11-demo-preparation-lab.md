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

---

# Playwright and Unified Dashboard Demo Preparation

The original performance demonstration remains valid. This section adds the Playwright workflow without replacing any existing demo assets.

## Additional objective

Prepare a repeatable Playwright demonstration that shows:

- Chromium, Firefox, and WebKit execution
- Parallel and cross-browser capability
- Retry configuration
- Browser-specific summaries
- Cross-browser aggregation
- Unified dashboard generation and validation

## Step 8 — Validate Playwright prerequisites

```bash
java -version
./gradlew tasks --all | grep -E "playwrightDemo|playwrightRegression|webCrossBrowserTest|generateCrossBrowserWebSummaries"
./gradlew playwrightInstall
```

Expected tasks include:

```text
playwrightDemo
playwrightRegression
webCrossBrowserTest
generateCrossBrowserWebSummaries
openPlaywrightDashboard
```

## Step 9 — Run the Playwright demo workflow

Use headed mode for a live demonstration:

```bash
./gradlew clean playwrightDemo \
  -Pheadless=false \
  -PwebThreads=4 \
  -PretryCount=1 \
  -Penvironment=DEMO \
  -PbuildNumber=demo-001 \
  -Pbranch=demo/playwright \
  -Pcommit=demo123 \
  -PopenDashboard=true
```

For a stable rehearsal or CI-style execution, use:

```bash
./gradlew clean playwrightDemo \
  -Pheadless=true \
  -PwebThreads=4 \
  -PretryCount=1 \
  -Penvironment=DEMO \
  -PbuildNumber=demo-001 \
  -Pbranch=demo/playwright \
  -Pcommit=demo123 \
  -PopenDashboard=false
```

## What `playwrightDemo` demonstrates

```text
Playwright browser setup
        |
        v
Chromium execution
        |
        v
Firefox execution
        |
        v
WebKit execution
        |
        v
Browser-specific summaries
        |
        v
Cross-browser aggregate summary
        |
        v
Unified dashboard generation
        |
        v
Optional dashboard launch
```

## Step 10 — Validate browser-specific summaries

```bash
find web/reports -maxdepth 1 -name "*-summary.json" -print
```

Verify the presence of:

```text
web/reports/chromium-summary.json
web/reports/firefox-summary.json
web/reports/webkit-summary.json
web/reports/cross-browser-summary.json
```

Review an individual summary:

```bash
cat web/reports/chromium-summary.json
```

Confirm that it shows a meaningful status and non-zero metrics after execution.

## Step 11 — Validate the cross-browser summary

```bash
cat web/reports/cross-browser-summary.json
```

Confirm:

- `status` reflects the combined execution result.
- `total` equals the total from all available browser summaries.
- `availableBrowsers` is `3` for a complete cross-browser run.
- `missingBrowsers` is `0`.
- Environment and build metadata match the command parameters.

## Step 12 — Validate the unified dashboard

Check that the dashboard exists:

```bash
test -f dashboard/reports/index.html && echo "Dashboard generated"
```

Open it manually on macOS when required:

```bash
open dashboard/reports/index.html
```

Dashboard validation checklist:

- Overall status is correct.
- Web and browser data are visible.
- Total, passed, failed, and skipped metrics match the JSON summaries.
- Chromium, Firefox, and WebKit results are represented.
- Environment, build number, branch, and commit are correct.
- No module is incorrectly shown as passed when its summary is missing.
- Links to available detailed reports work.

## Step 13 — Demonstrate flexible browser selection

All browsers:

```bash
./gradlew webSelectedBrowserTest -Pbrowser=ALL -Pheadless=true
```

Selected browsers:

```bash
./gradlew webSelectedBrowserTest -Pbrowser=CHROMIUM,FIREFOX -Pheadless=true
```

Single browser:

```bash
./gradlew webSelectedBrowserTest -Pbrowser=WEBKIT -Pheadless=true
```

Explain that unsupported values are rejected intentionally:

```bash
./gradlew webSelectedBrowserTest -Pbrowser=EDGE
```

## Step 14 — Recommended presenter sequence

1. Explain the updated MAPAF architecture.
2. Show Playwright test and page-object structure.
3. Explain browser selection, threads, headless mode, and retry count.
4. Run `playwrightDemo`.
5. Show Chromium, Firefox, and WebKit execution.
6. Review browser-specific summaries.
7. Review `cross-browser-summary.json`.
8. Open and validate the unified dashboard.
9. Explain how the same workflow runs in CI/CD.
10. Close with the AI-assisted locator and healing roadmap.

## Playwright fallback plan

- Keep a previously generated dashboard and JSON summaries available.
- Use headless mode if headed execution is affected by screen-sharing or desktop policy.
- Run `-Pbrowser=CHROMIUM` when demonstration time is limited.
- Run `playwrightRegression` if automatic dashboard opening causes an environment issue.
- Never edit summary files manually to manufacture a successful result.

## Updated completion criteria

- Existing API and performance demo assets remain available.
- `playwrightDemo` completes successfully.
- All three browser summaries are generated.
- Cross-browser aggregate metrics are correct.
- Unified dashboard opens and displays the expected KPIs.
- The presenter can explain execution, reporting, and CI/CD flows.

## Troubleshooting

If the AI-assisted healing demo does not complete successfully, refer to **Framework Lab 12 – Locator Discovery and AI Healing** for common configuration issues, API integration guidance, and troubleshooting steps.
