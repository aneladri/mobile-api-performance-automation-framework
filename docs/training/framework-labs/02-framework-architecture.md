# Lab 02 – Framework Architecture

## Goal

By the end of this lab, you will understand:

* What an automation framework is
* Why MAPAF exists
* How MAPAF is structured
* How tests execute
* How reports are generated
* How AI analysis works

---

# What is a Framework?

A framework is a reusable structure that helps engineers build tests consistently.

Without a framework:

```text
Test 1
 ├── Driver Setup
 ├── Configuration
 ├── Logging
 └── Validation

Test 2
 ├── Driver Setup
 ├── Configuration
 ├── Logging
 └── Validation
```

Every test duplicates code.

With a framework:

```text
Framework
 ├── DriverFactory
 ├── ConfigManager
 ├── Reporting
 ├── Utilities
 └── Base Classes

Tests
 └── Only contain business logic
```

---

# Why Do We Need MAPAF?

Most organizations maintain:

* API Framework
* Mobile Framework
* Performance Framework

separately.

This creates:

* Duplicate code
* Duplicate reports
* Duplicate maintenance effort

MAPAF combines them into a single platform.

---

# High-Level Architecture

```text
MAPAF
│
├── API Layer
├── Mobile Layer
├── Performance Layer
├── AI Layer
├── Reporting Layer
└── Training Layer
```

---

# Project Structure

```text
src/test/java
│
├── api
├── mobile
├── core
├── ai
└── performance
```

---

# Core Layer

## What?

The Core Layer contains reusable framework functionality.

Examples:

```text
DriverFactory
ConfigManager
WaitUtils
LoggerUtil
```

---

## Why?

Without the Core Layer:

Every test must manage:

* Driver creation
* Configuration
* Logging

individually.

---

# API Layer

## What?

Contains API tests.

Example:

```text
HealthCheckTest
AuthenticationTest
UserApiTest
```

---

## How?

Flow:

```text
Test
↓
BaseApiClient
↓
Endpoint
↓
ResponseValidator
```

---

# Mobile Layer

## What?

Contains Android and iOS automation.

Structure:

```text
mobile
│
├── screens
├── tests
└── locators
```

---

## How?

Flow:

```text
Test
↓
Screen Object
↓
BaseScreen
↓
Appium Driver
↓
Mobile App
```

---

# Performance Layer

## What?

Contains k6 tests.

Examples:

```text
Smoke Test
Load Test
Stress Test
```

---

## How?

Flow:

```text
k6
↓
API Endpoint
↓
JSON Summary
↓
Performance Analysis
```

---

# AI Layer

## What?

Provides intelligent analysis.

Examples:

```text
Failure Analysis
Performance Analysis
Healing Recommendations
Report Generation
```

---

## How?

Flow:

```text
Failure
↓
Agent
↓
Analysis
↓
Report
```

---

# Reporting Layer

## What?

Generates reports.

Examples:

```text
Allure
AI Reports
Performance Reports
```

---

# End-to-End Example

## Mobile Test Execution

```text
IOSLaunchTest
↓
IOSSettingsScreen
↓
BaseScreen
↓
DriverFactory
↓
Appium
↓
iOS Simulator
```

---

## Failure Analysis Flow

```text
Test Failure
↓
TestListener
↓
FailureAnalysisAgent
↓
ReportWriter
↓
failure-<test>.md
```

---

# Common Mistakes

## Mistake

Creating locators directly in tests.

### Why wrong?

Violates Page Object Model.

### Correct

Place locators inside Screen Objects.

---

## Mistake

Creating drivers inside tests.

### Why wrong?

DriverFactory already handles this.

---

# Checkpoint

The trainee should be able to explain:

1. What is an automation framework?
2. Why does MAPAF exist?
3. What are the major layers?
4. How does a mobile test execute?
5. How does AI analysis execute?
6. What is the responsibility of the Core Layer?

---

# MAPAF v2.0 Updated Architecture

MAPAF v2.0 extends the original API, mobile, performance, AI, and reporting capabilities with an enterprise Playwright engine and a unified quality dashboard.

```text
Users and Delivery Pipelines
        |
        v
Gradle Tasks / CLI / GitHub Actions / Azure DevOps
        |
        v
Execution Orchestration
  |-- Sequential execution
  |-- Parallel execution
  |-- Selected-browser execution
  |-- Cross-browser execution
  |-- Retry control
  |-- Regression workflow
  `-- Demo workflow
        |
        +----------------+----------------+----------------+
        |                |                |                |
        v                v                v                v
   API Automation   Web Automation   Mobile Automation   Performance
   REST Assured     Playwright       Appium              k6 / JMeter
        |                |                |                |
        +----------------+----------------+----------------+
                         |
                         v
              Standardised JSON Summaries
                         |
                         v
              Unified Dashboard Generator
                         |
                         v
           HTML Dashboard and CI/CD Artifacts
```

## Framework capability map

| Capability | Primary implementation | Output |
|---|---|---|
| API functional testing | REST Assured and TestNG | API execution summary |
| Web automation | Playwright for Java | Browser and test results |
| Android and iOS | Appium | Mobile execution results |
| Performance | k6 and JMeter | Performance summaries and HTML reports |
| Reporting | Java summary generators | Standardised JSON contracts |
| Unified dashboard | Dashboard generator | `dashboard/reports/index.html` |
| AI roadmap | Claude-compatible AI services | Analysis, locator suggestions, and healing recommendations |

# Playwright Engine Architecture

```text
TestNG Test
    |
    v
BaseWebTest
    |
    v
Playwright Manager
    |-- Playwright instance
    |-- Browser instance
    |-- BrowserContext
    `-- Page
    |
    v
Page Objects / Components
    |
    v
Application Under Test
```

For parallel execution, each worker must own isolated Playwright resources.

```text
Worker 1 --> ThreadLocal Playwright --> Context 1 --> Page 1
Worker 2 --> ThreadLocal Playwright --> Context 2 --> Page 2
Worker 3 --> ThreadLocal Playwright --> Context 3 --> Page 3
Worker 4 --> ThreadLocal Playwright --> Context 4 --> Page 4
```

This design prevents page, browser-context, and session state from leaking between tests.

## Supported Playwright execution modes

| Mode | Gradle task | Purpose |
|---|---|---|
| Standard web suite | `webTest` | Normal Playwright execution |
| Parallel suite | `webParallelTest` | Multi-threaded execution |
| Chromium only | `webChromiumTest` | Chromium validation |
| Firefox only | `webFirefoxTest` | Firefox validation |
| WebKit only | `webWebkitTest` | WebKit validation |
| All supported browsers | `webCrossBrowserTest` | Chromium, Firefox, and WebKit |
| Selected browsers | `webSelectedBrowserTest` | Run the value supplied through `-Pbrowser` |
| Regression workflow | `playwrightRegression` | Installation, execution, reporting, and dashboard generation |
| Demo workflow | `playwrightDemo` | Complete headed or headless demonstration flow |

# Reporting Pipeline Architecture

```text
Test execution
    |
    v
TestNG XML and module result files
    |
    +--> Chromium summary ----+
    +--> Firefox summary -----+--> Cross-browser aggregate summary
    +--> WebKit summary ------+              |
    |                                        v
    +--> API summary                    Unified dashboard
    +--> Performance summary                 |
    `--> Other module summaries              v
                                     HTML report and artifacts
```

## Browser-specific summaries

The Playwright reporting layer generates:

```text
web/reports/chromium-summary.json
web/reports/firefox-summary.json
web/reports/webkit-summary.json
```

Each summary contains the browser status and execution metrics such as total, passed, failed, skipped, and available result files.

## Cross-browser aggregate summary

The aggregate output is:

```text
web/reports/cross-browser-summary.json
```

It combines all available browser summaries and reports:

- Overall status
- Total tests
- Passed, failed, and skipped tests
- Available browsers
- Missing browsers
- Build and environment metadata

# Unified Dashboard Architecture

```text
API summary --------------------+
Performance summary ------------+
Web/browser summaries ----------+--> Summary reader
Cross-browser aggregate --------+        |
Mobile summary, when available -+        v
                                  Dashboard model
                                         |
                                         v
                              HTML/CSS/JavaScript builder
                                         |
                                         v
                           dashboard/reports/index.html
```

The dashboard is intentionally summary-driven. A module integrates with the dashboard by producing a summary that follows the reporting contract rather than by directly changing the HTML.

## Dashboard generation principles

1. Tests produce raw results.
2. Module-specific generators convert raw results to stable JSON summaries.
3. Aggregators combine related summaries where required.
4. The dashboard generator reads the summaries.
5. The resulting dashboard is published as a CI/CD artifact.

# Execution Flow

```text
Developer or pipeline starts a Gradle task
        |
        v
Configuration is resolved
(browser, headless, threads, retry, environment, build, branch, commit)
        |
        v
Playwright browsers are installed or validated
        |
        v
Selected browser suites execute
        |
        v
Retry analyser handles eligible failures
        |
        v
Browser summaries are generated
        |
        v
Cross-browser summary is generated
        |
        v
Unified dashboard is generated
        |
        v
Dashboard is opened locally or uploaded as an artifact
```

# CI/CD Flow

```text
Git commit / Pull request
        |
        v
GitHub Actions or Azure DevOps
        |
        v
Java and Node prerequisites
        |
        v
./gradlew playwrightRegression
        |
        v
Cross-browser tests and summaries
        |
        v
Unified dashboard
        |
        v
Publish test results and dashboard artifacts
```

Recommended metadata parameters:

```bash
-Penvironment=QA \
-PbuildNumber="$BUILD_NUMBER" \
-Pbranch="$BRANCH_NAME" \
-Pcommit="$COMMIT_SHA"
```

# Future AI Integration Architecture

The future AI layer should remain an advisory and controlled framework service.

```text
Test failure or locator issue
        |
        v
Evidence collector
(logs, screenshot, DOM, hierarchy, browser, stack trace)
        |
        v
AI failure analyser
        |
        +--> Root-cause classification
        +--> Locator candidates
        +--> Code recommendation
        `--> Risk and confidence score
        |
        v
Policy and validation gate
        |
        +--> Human approval
        +--> Uniqueness validation
        +--> Compile check
        `--> Targeted smoke test
        |
        v
Approved update / controlled retry
```

## AI guardrails

- Never invent a locator without evidence from the DOM or mobile hierarchy.
- Keep generated suggestions separate from approved framework assets.
- Require uniqueness checks for locator candidates.
- Record the original locator, proposed locator, confidence, and reason.
- Use a controlled retry limit to avoid hiding real failures.
- Preserve auditability for every accepted AI recommendation.

# Architecture Review Exercise

Explain how a test moves from `webSelectedBrowserTest` to the unified dashboard. Your explanation should identify:

1. Browser selection
2. Test execution
3. Retry handling
4. Browser-specific summary generation
5. Cross-browser aggregation
6. Dashboard generation
7. CI/CD artifact publication
