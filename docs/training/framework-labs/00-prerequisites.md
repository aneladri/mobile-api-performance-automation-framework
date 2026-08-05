# Lab 00 – Introduction to MAPAF

## Goal

By the end of this lab, you will understand:

* What MAPAF is
* Why MAPAF was created
* What problems MAPAF solves
* The major capabilities of the platform
* The learning journey ahead

---

# What is MAPAF?

MAPAF stands for:

```text
Mobile API Performance Automation Framework
```

MAPAF is an automation platform that combines:

* API Testing
* Android Testing
* iOS Testing
* Performance Testing
* AI-Assisted Analysis

into a single framework.

---

# Why Was MAPAF Created?

Traditional automation frameworks usually focus on only one area:

Example:

```text
Framework A
└── UI Testing

Framework B
└── API Testing

Framework C
└── Performance Testing
```

This creates:

* Duplicate code
* Multiple reports
* Multiple tools
* Higher maintenance effort

MAPAF solves this by providing a unified platform.

---

# What Problems Does MAPAF Solve?

## Problem 1

Too many testing tools.

Solution:

Single framework.

---

## Problem 2

Slow failure investigation.

Solution:

AI-assisted failure analysis.

---

## Problem 3

Performance testing disconnected from automation.

Solution:

Built-in k6 integration.

---

## Problem 4

Difficult onboarding.

Solution:

Training platform and guided labs.

---

# MAPAF Architecture Overview

```text
MAPAF
│
├── API Automation
├── Android Automation
├── iOS Automation
├── Performance Testing
├── Reporting
├── AI Layer
└── Training Platform
```

---

# MAPAF Layers

## Framework Layer

Responsible for:

* Driver creation
* Configuration
* Reporting
* Test execution

---

## Automation Layer

Responsible for:

* API Tests
* Android Tests
* iOS Tests

---

## Performance Layer

Responsible for:

* Smoke Tests
* Load Tests
* Stress Tests

---

## AI Layer

Responsible for:

* Failure Analysis
* Healing Recommendations
* Performance Analysis
* Report Generation

---

# What Will You Learn?

## Module 1

Project Setup

---

## Module 2

Framework Architecture

---

## Module 3

API Automation

---

## Module 4

Android Automation

---

## Module 5

iOS Automation

---

## Module 6

Performance Testing

---

## Module 7

AI-Assisted Analysis

---

## Module 8

Debugging

---

## Module 9

Git Workflow

---

## Module 10

Capstone Project

---

# Success Criteria

At the end of training you should be able to:

* Create API tests
* Create Android tests
* Create iOS tests
* Execute performance tests
* Understand AI reports
* Debug failures
* Contribute to MAPAF

---

# Checkpoint

The trainee should be able to answer:

1. What does MAPAF stand for?
2. Why was MAPAF created?
3. What are the major layers of MAPAF?
4. What testing types does MAPAF support?
5. What is the purpose of the AI layer?

# Lab 00 — Prerequisites

## Core tools

```bash
java -version
./gradlew --version
node --version
```

## Performance tools — Portable mode

### k6

Verify:

```bash
k6 version
```

Install through an approved company mechanism when not available.

### JMeter on a managed Mac

Do not change Homebrew ownership. Use the portable archive.

```bash
mkdir -p "$HOME/Tools"
tar -xzf "$HOME/Downloads/apache-jmeter-5.6.3.tgz" -C "$HOME/Tools"
chmod +x "$HOME/Tools/apache-jmeter-5.6.3/bin/jmeter"
export JMETER_HOME="$HOME/Tools/apache-jmeter-5.6.3"
export PATH="$JMETER_HOME/bin:$PATH"
```

Verify:

```bash
jmeter --version
bash performance/scripts/resolve-jmeter.sh
```

## Performance tools — Docker mode

```bash
docker version
docker compose version
```

## Completion criteria

- Java and Gradle wrapper work.
- Node.js is available for portable mock API execution.
- k6 and portable JMeter are available, or Docker mode is approved.
- `./gradlew clean compileTestJava` succeeds.
