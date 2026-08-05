# Lab 07 – AI Analysis

## Goal

By the end of this lab, you will understand:

* What the MAPAF AI Layer is
* Why AI was added to MAPAF
* How AI agents work
* How failure analysis works
* How performance analysis works
* How healing recommendations work
* How AI reports are generated

---

# Why Does MAPAF Have an AI Layer?

Traditional automation frameworks execute tests.

Example:

```text
Test
↓
PASS / FAIL
```

The engineer must investigate failures manually.

MAPAF extends this flow:

```text
Test
↓
PASS / FAIL
↓
AI Analysis
↓
Recommendation
↓
Report
```

The goal is to reduce debugging time.

---

# What is an AI Agent?

An AI Agent is a specialized analysis component.

Examples:

```text
Failure Analysis Agent
Performance Analysis Agent
Healing Analysis Agent
Documentation Agent
Architect Agent
```

Each agent has a specific responsibility.

---

# MAPAF AI Architecture

```text
AI Layer
│
├── Failure Analysis
├── Performance Analysis
├── Healing Analysis
├── Reporting
├── Metrics
└── Orchestration
```

---

# Failure Analysis

## What?

Analyzes test failures.

Example:

```text
SSLHandshakeException
```

Output:

```text
Classification:
SSL Configuration Failure

Recommendation:
Import certificates into trust store.
```

---

# How Failure Analysis Works

```text
Test Failure
↓
TestListener
↓
FailureAnalysisAgent
↓
ReportWriter
↓
failure-test-name.md
```

---

# Example Failure Types

MAPAF currently recognizes:

```text
SSLHandshakeException

BrowserStack Limit Exceeded

ANDROID_HOME Missing

SessionNotCreatedException

WebDriverAgent Failures
```

---

# Healing Analysis

## What?

Provides self-healing recommendations.

Example:

```text
NoSuchElementException
```

Output:

```text
Locator Recommendation:
accessibilityId=<stable-id>

Wait Recommendation:
waitForVisible()

Confidence:
88%
```

---

# Healing Architecture

```text
Broken Locator
↓
HealedLocatorStore
↓
LocalHealingRuleEngine
↓
HealingBudgetGuard
↓
Claude (future)
```

---

# Why Healing Exists

Locators often fail because:

```text
ID changed
Text changed
Accessibility ID changed
```

Healing attempts to recover automatically before the test fails.

---

# Performance Analysis

## What?

Analyzes k6 performance results.

Example:

```text
Baseline p95:
40ms

Current p95:
70ms
```

Output:

```text
Classification:
REGRESSION
```

---

# Performance Analysis Flow

```text
k6
↓
smoke-summary.json
↓
K6SummaryParser
↓
PerformanceRegressionAnalyzer
↓
PerformanceAnalysisAgent
↓
Report
```

---

# Performance Classifications

Possible outcomes:

```text
PASS

WARNING

REGRESSION

CRITICAL
```

---

# AI Reports

MAPAF automatically generates:

```text
Failure Reports

Performance Reports

Healing Reports

Metrics Reports
```

Location:

```text
reports/ai/generated/
```

---

# AI Metrics

MAPAF tracks:

```text
Agent Runs

Successful Analyses

Unknown Classifications

Average Confidence
```

Purpose:

Measure AI effectiveness.

---

# AI Execution History

MAPAF records:

```text
Agent Name

Classification

Confidence
```

Purpose:

Trend analysis and future dashboards.

---

# What is the Orchestrator?

The Orchestrator coordinates multiple agents.

Flow:

```text
Failure
↓
Failure Analysis

Documentation Analysis

Performance Analysis

Healing Analysis
↓
Unified Recommendation
```

---

# Current AI Limitations

Current MAPAF AI is primarily:

```text
Rule-Based
```

Examples:

```java
if(log.contains("SSLHandshakeException"))
```

Future roadmap:

```text
Rule Engine
↓
LLM Integration
```

---

# Common Mistakes

## Mistake

Assuming AI fixes problems automatically.

Reality:

```text
Current:
Recommendations

Future:
Automated Fixes
```

---

## Mistake

Ignoring confidence scores.

Confidence indicates:

```text
How reliable the recommendation is.
```

---

# Troubleshooting

## Unknown Failure

Output:

```text
Unknown Failure
```

Meaning:

No matching rule exists.

Action:

Add new pattern to the analysis engine.

---

## Missing Report

Check:

```text
TestListener
ReportWriter
reports/ai/generated/
```

---

# End-to-End Example

Input:

```text
NoSuchElementException
```

Flow:

```text
Failure
↓
HealingAnalysisAgent
↓
UnifiedHealingAdvisor
↓
Healing Report
```

Output:

```text
Suggested Locator:
accessibilityId=loginButton

Suggested Wait:
waitForVisible()

Confidence:
88%
```

---

# Checkpoint

The trainee should be able to answer:

1. Why does MAPAF have an AI layer?
2. What is an AI Agent?
3. What does FailureAnalysisAgent do?
4. What does PerformanceAnalysisAgent do?
5. What is locator healing?
6. What is the Orchestrator?
7. Where are AI reports stored?
8. What do confidence scores mean?
9. What are the current limitations of MAPAF AI?
10. How does AI help engineers debug failures?

