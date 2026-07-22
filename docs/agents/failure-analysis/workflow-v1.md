# Claude Failure Analysis Workflow v1

## Purpose

Analyze automation failures and provide a structured diagnosis.

---

# Inputs

The agent may receive one or more of the following:

## Execution Logs

* Gradle Console Output
* GitHub Actions Logs
* Appium Logs
* BrowserStack Logs
* K6 Output

## Test Artifacts

* TestNG Results
* Allure Results
* Screenshots
* Videos

## Framework Data

* Environment Configuration
* Device Configuration
* Execution Mode
* Test Data

---

# Processing Steps

## Step 1 - Failure Classification

Classify failure using:

```text
Environment Configuration
Dependency Resolution
SSL / Certificate
Appium Driver Failure
BrowserStack Session Failure
Locator Failure
Synchronization Failure
API Authentication Failure
API Test Data Failure
Performance Threshold Failure
CI/CD Failure
Infrastructure Failure
```

Reference:

```text
docs/agents/failure-analysis/classifications.md
```

---

## Step 2 - Evidence Collection

Extract:

* Exception Type
* Error Message
* Stack Trace
* Affected Component
* File Name
* Line Number

Example:

```text
Exception:
SessionNotCreatedException

Evidence:
Neither ANDROID_HOME nor ANDROID_SDK_ROOT environment variable was exported
```

---

## Step 3 - Root Cause Analysis

Determine:

```text
Immediate Cause
Underlying Cause
System Impact
```

Example:

```text
Immediate Cause:
ANDROID_HOME missing

Underlying Cause:
Android SDK path not exported before Appium startup

System Impact:
All Android Appium sessions fail
```

---

## Step 4 - Suggested Fix

Provide:

```text
Short Fix
Long-Term Fix
Preventive Action
```

Example:

```text
Short Fix:
Export ANDROID_HOME

Long-Term Fix:
Validate SDK path during framework startup

Preventive Action:
Add environment validation utility
```

---

## Step 5 - Confidence Scoring

Scale:

```text
90-100%
Known failure pattern

70-89%
Strong evidence

50-69%
Likely cause

Below 50%
Insufficient information
```

---

# Output Format

```text
Failure Type:

Root Cause:

Evidence:

Suggested Fix:

Confidence Score:

Recommended Owner:

Recommended Next Action:
```

---

# Success Criteria

The agent must:

* Correctly classify failures
* Provide supporting evidence
* Avoid unsupported assumptions
* Recommend actionable fixes
* Assign confidence score

