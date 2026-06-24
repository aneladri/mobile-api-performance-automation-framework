# MAPAF - Mobile API Performance Automation Framework

## Framework Owner

**Aneesh Neladri**
Automation Manager | Test Architect | Quality Engineering Leader

---

## Overview

MAPAF (Mobile API Performance Automation Framework) is an enterprise-grade Quality Engineering platform that combines:

* API Automation
* Android Automation
* iOS Automation
* Performance Testing
* AI-Assisted Analysis
* Locator Healing
* Grafana Observability
* CI/CD Automation
* Training & Enablement

## Overview

MAPAF (Mobile API Performance Automation Framework) is an enterprise-grade Quality Engineering platform that combines:

* API Automation
* Android Automation
* iOS Automation
* Performance Testing
* AI-Assisted Analysis
* Locator Healing
* Grafana Observability
* CI/CD Automation
* Training & Enablement

MAPAF provides a single platform for test execution, reporting, AI-driven analysis, performance observability, and engineer onboarding.

---

## Framework Capabilities

### API Automation

* REST Assured
* Authentication Management
* Token Management
* Schema Validation
* Request Builder Pattern
* Test Data Management

### Mobile Automation

* Appium 2.x
* Android Automation
* iOS Automation
* Screen Object Pattern
* Business Flow Layer
* Screenshot Management
* BrowserStack Execution
* Self-Healing Locator Framework

### Performance Testing

* Smoke Testing
* Load Testing
* Stress Testing
* Soak Testing
* Threshold Validation
* Regression Detection

### AI Engineering Layer

* Failure Analysis Agent
* Performance Analysis Agent
* Healing Analysis Agent
* Unified Healing Advisor
* AI Report Generation
* Agent Metrics & History

### Locator Healing

* Healed Locator Store
* Local Healing Rule Engine
* Healing Budget Guard
* Page Source Compression
* Healing Metrics
* Healing Reports

### Reporting & Observability

* Allure Reports
* AI Reports
* Healing Reports
* Performance Reports
* Grafana Dashboards
* InfluxDB Metrics Storage

### Training Platform

* 10 Guided Labs
* Trainer Guide
* Training Plan
* Capstone Project
* AI Healing Exercises

---

## Technology Stack

| Layer           | Technology     |
| --------------- | -------------- |
| Language        | Java 17        |
| Build Tool      | Gradle         |
| Test Framework  | TestNG         |
| Mobile          | Appium 2       |
| API             | REST Assured   |
| Performance     | k6             |
| Reporting       | Allure         |
| Observability   | Grafana        |
| Metrics Storage | InfluxDB       |
| CI/CD           | GitHub Actions |
| Cloud Execution | BrowserStack   |
| Source Control  | Git            |

---

## Architecture

```text
MAPAF
│
├── Core Layer
├── API Layer
├── Mobile Layer
├── Performance Layer
├── AI Layer
├── Healing Layer
├── Reporting Layer
├── Observability Layer
├── CI/CD Layer
└── Training Platform
```

---

## Project Structure

```text
src/test/java
│
├── api
├── mobile
├── core
├── ai
└── performance

performance/
│
├── smoke
├── load
├── stress
├── soak
├── auth
└── config

docs/
│
├── training
├── performance
├── agents
└── decisions
```

---

## Quick Start

Clone:

```bash
git clone <repository-url>
cd automation-framework
```

Compile:

```bash
./gradlew clean compileTestJava
```

Run API Tests:

```bash
./gradlew apiTest
```

Run Mobile Tests:

```bash
./gradlew mobileTest
```

Run iOS Tests:

```bash
./gradlew iosTest
```

Run AI Tests:

```bash
./gradlew aiTest
```

Run Smoke Performance Test:

```bash
k6 run performance/k6/smoke/api-health-smoke.js
```

Generate Allure Report:

```bash
allure serve build/allure-results
```

---

## AI Locator Healing

MAPAF includes a tiered locator healing architecture:

```text
Broken Locator
↓
Cache
↓
Local Rule Engine
↓
Budget Guard
↓
AI Recommendation
```

Healing metrics are automatically tracked and reported.

---

## Grafana Observability

MAPAF supports:

```text
k6
↓
InfluxDB
↓
Grafana
↓
Performance Dashboard
```

Dashboard Metrics:

* Response Time
* Request Rate
* Error Rate
* Virtual Users
* Iterations

---

## Training Programme

Training Path:

```text
Lab 00 – Introduction
Lab 01 – Setup
Lab 02 – Architecture
Lab 03 – API Testing
Lab 04 – Android Testing
Lab 05 – iOS Testing
Lab 06 – Performance Testing
Lab 07 – AI Analysis
Lab 07b – AI Healing Exercise
Lab 08 – Debugging
Lab 09 – Git Workflow
Lab 10 – Capstone Project
```

---

## Documentation

Training:

```text
docs/training/framework-labs/
```

Performance:

```text
docs/performance/
```

Architecture Decisions:

```text
docs/DECISIONS/
```

AI Documentation:

```text
docs/agents/
```

---

## Ownership

### Framework Owner

**Aneesh Neladri**
Automation Manager | Test Architect | Quality Engineering Leader

### Responsibilities

* Framework Architecture
* Automation Standards
* AI Platform
* Performance Platform
* Training Programme
* CI/CD Governance
* Release Management

---

## Current Version

```text
MAPAF v3.0
```

### Status

```text
Production Ready
Active Development
