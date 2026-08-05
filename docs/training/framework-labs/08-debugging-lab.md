# Lab 08 – Debugging & Troubleshooting

## Goal

By the end of this lab, you will understand:

* How to investigate failures
* How to read logs
* How to use Allure reports
* How to debug API failures
* How to debug Android failures
* How to debug iOS failures
* How to debug performance failures
* How to use MAPAF AI reports during investigations

---

# Why is Debugging Important?

Most engineering time is spent investigating failures rather than writing tests.

Example:

```text id="dbg1"
Test Creation:
30 minutes

Failure Investigation:
3 hours
```

Good debugging skills reduce investigation time significantly.

---

# Debugging Mindset

Never start with:

```text id="dbg2"
The framework is broken.
```

Start with:

```text id="dbg3"
What failed?
Why did it fail?
What evidence do I have?
```

---

# Failure Investigation Flow

```text id="dbg4"
Failure
↓
Logs
↓
Screenshots
↓
Reports
↓
AI Analysis
↓
Root Cause
```

---

# Step 1 – Identify the Failure

Questions:

```text id="dbg5"
Which test failed?

When did it fail?

What was the expected result?

What was the actual result?
```

---

# Step 2 – Review Logs

Location:

```text id="dbg6"
Console Output

Build Logs

CI/CD Logs
```

Look for:

```text id="dbg7"
ERROR

Exception

Stack Trace
```

---

# Understanding Stack Traces

Example:

```text id="dbg8"
NoSuchElementException
```

Meaning:

```text id="dbg9"
Locator could not find the element.
```

---

Example:

```text id="dbg10"
SessionNotCreatedException
```

Meaning:

```text id="dbg11"
Appium failed to create a session.
```

---

# Step 3 – Review Allure Reports

Location:

```bash id="dbg12"
allure serve build/allure-results
```

Look for:

```text id="dbg13"
Screenshots

Logs

Attachments

Failure Details
```

---

# API Failure Debugging

## Common Error

```text id="dbg14"
401 Unauthorized
```

Check:

```text id="dbg15"
Token

Credentials

AuthManager
```

---

## Common Error

```text id="dbg16"
404 Not Found
```

Check:

```text id="dbg17"
Endpoint

Base URL

Environment
```

---

## Common Error

```text id="dbg18"
500 Internal Server Error
```

Check:

```text id="dbg19"
Backend Service

Logs

Environment Health
```

---

# Android Failure Debugging

## Common Error

```text id="dbg20"
Device not found
```

Check:

```bash id="dbg21"
adb devices
```

Expected:

```text id="dbg22"
emulator-5554
device
```

---

## Common Error

```text id="dbg23"
ANDROID_HOME missing
```

Check:

```bash id="dbg24"
echo $ANDROID_HOME
```

---

## Common Error

```text id="dbg25"
NoSuchElementException
```

Check:

```text id="dbg26"
Locator

Screen State

Wait Strategy
```

---

# iOS Failure Debugging

## Common Error

```text id="dbg27"
Unable to find a destination matching
```

Check:

```bash id="dbg28"
xcrun simctl list devices
```

---

## Common Error

```text id="dbg29"
WebDriverAgent failure
```

Check:

```text id="dbg30"
Xcode

Simulator

XCUITest
```

---

## Common Error

```text id="dbg31"
SessionNotCreatedException
```

Verify:

```text id="dbg32"
Appium

Capabilities

Simulator
```

---

# Performance Failure Debugging

## Threshold Failure

Example:

```text id="dbg33"
p95 threshold failed
```

Check:

```text id="dbg34"
Current p95

Baseline p95
```

---

## Regression

Example:

```text id="dbg35"
Baseline:
40ms

Current:
70ms
```

Result:

```text id="dbg36"
REGRESSION
```

---

## Error Rate Failure

Example:

```text id="dbg37"
Error Rate:
12%
```

Check:

```text id="dbg38"
Backend

Dependencies

Environment
```

---

# AI Failure Analysis

MAPAF automatically generates:

```text id="dbg39"
Failure Reports
```

Example:

```text id="dbg40"
SSLHandshakeException
```

Output:

```text id="dbg41"
Classification:
SSL Configuration Failure

Recommendation:
Import certificates into trust store.
```

---

# Healing Recommendations

Example:

```text id="dbg42"
NoSuchElementException
```

Output:

```text id="dbg43"
Suggested Locator:
accessibilityId=loginButton

Suggested Wait:
waitForVisible()
```

---

# Root Cause Analysis

Always classify failures into categories:

```text id="dbg44"
Framework Issue

Environment Issue

Application Issue

Test Data Issue

Performance Issue
```

---

# Common Mistakes

## Mistake

Fixing symptoms instead of causes.

Wrong:

```text id="dbg45"
Increase wait time
```

Correct:

```text id="dbg46"
Understand why the element was not found.
```

---

## Mistake

Ignoring screenshots.

Screenshots often reveal the actual issue.

---

## Mistake

Ignoring AI reports.

AI reports provide useful investigation starting points.

---

# Troubleshooting Checklist

Before escalating:

```text id="dbg47"
✓ Review logs

✓ Review screenshot

✓ Review Allure

✓ Review AI report

✓ Verify environment

✓ Re-run test
```

---

# Real Example

Failure:

```text id="dbg48"
NoSuchElementException
```

Investigation:

```text id="dbg49"
Screenshot
↓
Element not visible

AI Report
↓
Locator Recommendation

Root Cause
↓
Application screen changed
```

Resolution:

```text id="dbg50"
Update locator
```

---

# Checkpoint

The trainee should be able to answer:

1. What is the first step in debugging?
2. How do you read a stack trace?
3. How do you debug API failures?
4. How do you debug Android failures?
5. How do you debug iOS failures?
6. How do you debug performance failures?
7. How do AI reports help debugging?
8. What is root cause analysis?
9. What evidence should be collected before escalation?
10. How do you use Allure during investigations?

