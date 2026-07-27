# Lab 14 — Unified Dashboard Training

## Objective

Understand how MAPAF converts module and browser execution results into a single quality dashboard.

# Dashboard Architecture

```text
Raw test results
    |
    v
Module summary generators
    |
    +--> API summary
    +--> Performance summary
    +--> Chromium summary
    +--> Firefox summary
    +--> WebKit summary
    `--> Other module summaries
    |
    v
Cross-browser aggregator
    |
    v
Unified dashboard generator
    |
    v
dashboard/reports/index.html
```

# Summary Generation

Browser summaries can be generated individually or together:

```bash
./gradlew generateChromiumWebSummary
./gradlew generateFirefoxWebSummary
./gradlew generateWebkitWebSummary
./gradlew generateCrossBrowserWebSummaries
```

Because each summary task depends on its browser test task, the summary reflects a real execution rather than a missing-result placeholder.

# Browser-Specific Summaries

Expected files:

```text
web/reports/chromium-summary.json
web/reports/firefox-summary.json
web/reports/webkit-summary.json
```

Review:

```bash
cat web/reports/chromium-summary.json
```

Key fields to inspect:

- Status
- Total
- Passed
- Failed
- Skipped
- Result-file count
- Environment and build metadata

# Aggregate Summary

Generate the aggregate:

```bash
./gradlew generateCrossBrowserWebSummaries
```

Expected output:

```text
web/reports/cross-browser-summary.json
```

The aggregate combines metrics from all available browser summaries. It must not report `PASS` with zero tests when expected results are missing.

# Dashboard Generation Flow

```text
Browser tests
   |
   v
Browser summaries
   |
   v
Cross-browser summary
   |
   v
Other module summaries
   |
   v
Dashboard generator
   |
   v
Unified dashboard
```

Use the complete regression workflow:

```bash
./gradlew clean playwrightRegression \
  -PwebThreads=4 \
  -Pheadless=true \
  -PretryCount=1 \
  -Penvironment=QA \
  -PbuildNumber=local-001 \
  -Pbranch=feature/playwright \
  -Pcommit=abc123
```

# Reading KPIs

| KPI | Interpretation |
|---|---|
| Overall status | Combined result according to dashboard rules |
| Total | Number of executed test-result entries |
| Passed | Tests that completed successfully |
| Failed | Tests with final unsuccessful status |
| Skipped | Tests that did not execute to completion |
| Pass rate | Passed divided by relevant completed tests |
| Browser availability | Number of expected browser summaries found |
| Environment | Target environment supplied at execution time |
| Build/branch/commit | Traceability to the pipeline or developer run |

A retry pass should be interpreted separately from a first-attempt pass when retry detail is available. A future healed pass must also remain visible rather than being counted as an ordinary pass without context.

# Dashboard Validation

```bash
test -f dashboard/reports/index.html
open dashboard/reports/index.html
```

Validate the dashboard against source JSON files rather than relying on appearance alone.

Checklist:

- Totals match summary files.
- Overall status follows the failure rules.
- Missing summaries are visible or treated as unavailable.
- Browser metrics are not duplicated.
- Metadata is correct.
- Detailed-report links resolve.
- The dashboard can be published without local absolute paths.

# Troubleshooting

## Dashboard is empty

Check for summaries:

```bash
find . -name "*summary.json" -print
```

Then verify that dashboard generation runs after summary generation.

## Browser shows `NOT_RUN`

Run the corresponding browser test task before regenerating the summary. Confirm that TestNG XML files exist and contain executed tests.

## Aggregate total is zero

Inspect each browser summary. The aggregator cannot create valid test metrics from missing or empty browser results.

## Dashboard metadata is blank

Supply execution metadata:

```bash
-Penvironment=QA -PbuildNumber=123 -Pbranch=main -Pcommit=abc123
```

## Dashboard does not open

Generate it first, then use the operating-system command manually:

```bash
open dashboard/reports/index.html
```

For CI/CD, publish the directory as an artifact instead of attempting to open it.

# Extending the Dashboard

To add a new module:

1. Define its summary contract.
2. Generate deterministic JSON from raw results.
3. Represent missing and not-run states explicitly.
4. Add the summary to the dashboard reader.
5. Add a module card or detail section.
6. Add tests for pass, fail, empty, missing, and malformed summaries.
7. Publish supporting detailed reports as linked artifacts.

Future modules may include:

```text
Mobile
Accessibility
Security
AI analysis
AI healing
Historical trends
Release readiness
```

# Exercise

1. Run `playwrightRegression`.
2. Compare browser summaries with the cross-browser aggregate.
3. Compare the aggregate with dashboard KPIs.
4. Change the environment and build metadata and regenerate the dashboard.
5. Explain how a missing Firefox summary should be represented.
6. Propose a dashboard field for AI-assisted locator healing.

# Completion Criteria

The trainee can explain the complete reporting lifecycle, validate dashboard accuracy, troubleshoot missing data, and describe how a new automation module integrates with the dashboard.
