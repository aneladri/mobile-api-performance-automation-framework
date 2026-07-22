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

