# Lab 10 – MAPAF Capstone Project

## Goal

The Capstone Project validates that the trainee can independently use the MAPAF framework.

By the end of this project, the trainee should be able to:

* Create API tests
* Create Android tests
* Create iOS tests
* Execute performance tests
* Analyze failures
* Understand AI reports
* Use Git correctly
* Submit changes through the MAPAF workflow

---

# What is a Capstone Project?

A Capstone Project is a practical assessment.

Instead of learning concepts individually:

```text id="cap1"
API
Android
iOS
Performance
AI
Git
```

the trainee combines everything into a single project.

---

# Project Scenario

You have joined the Quality Engineering team.

The team asks you to:

```text id="cap2"
Create a complete validation suite
for a new application release.
```

You must:

* Create tests
* Execute tests
* Review reports
* Follow Git workflow

---

# Part 1 – API Validation

## Objective

Create a new API test.

Create:

```text id="cap3"
UserHealthCheckTest.java
```

Requirements:

```text id="cap4"
Call endpoint

Validate status code

Validate response
```

---

# Success Criteria

```text id="cap5"
Test passes

Code follows MAPAF standards
```

---

# Part 2 – Android Validation

## Objective

Create Android screen validation.

Requirements:

```text id="cap6"
Create Screen Object

Create Android test

Validate screen visibility
```

---

# Deliverables

```text id="cap7"
AndroidHomeScreen.java

AndroidLaunchTest.java
```

---

# Part 3 – iOS Validation

## Objective

Create iOS navigation validation.

Requirements:

```text id="cap8"
Settings
↓
General
↓
About
```

Validate:

```text id="cap9"
About screen visible
```

---

# Deliverables

```text id="cap10"
IOSSettingsScreen.java

IOSGeneralScreen.java

IOSAboutScreen.java
```

---

# Part 4 – Performance Validation

## Objective

Execute:

```text id="cap11"
Smoke Test

Load Test

Stress Test
```

Generate:

```text id="cap12"
Smoke Summary

Load Summary

Stress Summary
```

---

# Baseline Validation

Compare results against:

```text id="cap13"
performance/history/BASELINE.md
```

---

# Part 5 – AI Analysis

## Objective

Generate:

```text id="cap14"
Failure Report

Performance Report

Healing Report
```

Location:

```text id="cap15"
reports/ai/generated/
```

---

# Part 6 – Debugging Exercise

## Objective

Introduce a failure intentionally.

Examples:

```text id="cap16"
Wrong locator

Invalid endpoint

Wrong configuration
```

---

## Investigation

Use:

```text id="cap17"
Logs

Allure

AI Report
```

Determine:

```text id="cap18"
Root Cause
```

---

# Part 7 – Git Workflow

## Objective

Create feature branch.

Example:

```bash id="cap19"
git checkout -b feature/capstone-project
```

---

## Commit Work

Example:

```bash id="cap20"
git add .

git commit -m "Complete capstone project"
```

---

## Push

```bash id="cap21"
git push origin feature/capstone-project
```

---

# Part 8 – Final Review

Prepare summary:

```text id="cap22"
What was built?

What failed?

What was fixed?

What did AI recommend?

What was learned?
```

---

# Capstone Evaluation Matrix

| Area                | Weight |
| ------------------- | ------ |
| API Testing         | 15%    |
| Android Testing     | 15%    |
| iOS Testing         | 15%    |
| Performance Testing | 15%    |
| AI Analysis         | 15%    |
| Debugging           | 15%    |
| Git Workflow        | 10%    |

---

# Graduation Criteria

The trainee should be able to:

```text id="cap23"
Create API tests independently

Create Android tests independently

Create iOS tests independently

Run performance tests

Interpret AI reports

Debug failures

Use Git correctly
```

---

# MAPAF Engineer Certification

A trainee is considered MAPAF-ready when they can:

```text id="cap24"
Build

Execute

Debug

Analyze

Improve

the framework independently.
```

---

# Final Checkpoint

The trainee should be able to answer:

1. What is MAPAF?
2. How does MAPAF execute API tests?
3. How does MAPAF execute Android tests?
4. How does MAPAF execute iOS tests?
5. How does MAPAF execute performance tests?
6. How does MAPAF perform AI analysis?
7. How does MAPAF generate reports?
8. How does MAPAF debugging work?
9. How does the Git workflow operate?
10. How would you contribute a new feature to MAPAF?

# Lab 10 — Capstone Project

## Goal

Extend the framework with one functional or performance capability and deliver a repeatable demonstration.

## Performance capstone option

1. Add a deterministic mock API endpoint.
2. Add a k6 smoke scenario.
3. Add a JMeter smoke transaction with correlation/assertions where required.
4. Support Portable mode using `JMETER_HOME`/`-PjmeterHome`.
5. Validate Docker mode when available.
6. Generate and archive k6 and JMeter reports.
7. Update README, demo preparation, and the performance lab.

## Required evidence

- direct execution commands;
- Gradle commands;
- test result files;
- report screenshots or links;
- troubleshooting notes;
- assumptions and limitations;
- comparison of k6 and JMeter behavior.

## Acceptance criteria

- Clean setup from documented steps.
- Smoke profiles pass.
- Failure paths are intentional and explained.
- No secrets or real customer data are used.
- Another learner can reproduce the demo.
