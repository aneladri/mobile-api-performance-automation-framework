# MAPAF - Mobile API Performance Automation Framework

## Overview

MAPAF (Mobile API Performance Automation Framework) is an enterprise-grade automation platform supporting:

* Mobile Automation (Android & iOS)
* API Automation
* Performance Testing
* Cloud Execution
* CI/CD Integration
* Reporting & Observability
* AI-Assisted Engineering

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

* Appium 2
* Android Automation
* iOS Automation
* Page Object Model
* Business Flow Layer
* Screenshot Capture
* BrowserStack Execution

### Performance Testing

* K6 Smoke Testing
* Load Testing
* Stress Testing
* Threshold Validation

### Reporting

* Allure Reports
* Screenshots
* Logs
* CI/CD Artifacts

### CI/CD

* GitHub Actions
* API Pipeline
* Mobile Pipeline
* Performance Pipeline
* BrowserStack Pipeline

### AI Architecture

* Claude Failure Analysis Agent
* Claude Documentation Agent
* Claude Architect Agent

---

## Technology Stack

| Layer          | Technology     |
| -------------- | -------------- |
| Language       | Java 17        |
| Build Tool     | Gradle         |
| Test Framework | TestNG         |
| Mobile         | Appium 2       |
| API            | REST Assured   |
| Performance    | K6             |
| Reporting      | Allure         |
| CI/CD          | GitHub Actions |
| Cloud          | BrowserStack   |
| Source Control | Git            |

---

## Architecture

```text
MAPAF
│
├── Core Layer
├── API Layer
├── Mobile Layer
├── Performance Layer
├── Reporting Layer
├── CI/CD Layer
└── AI Agent Layer
```

For detailed architecture:

```text
docs/FRAMEWORK_ARCHITECTURE.md
```

---

## Quick Start

Clone:

```bash
git clone <repository-url>
cd automation-framework
```

Run API Tests:

```bash
gradle clean apiTest
```

Run Mobile Tests:

```bash
gradle clean mobileTest
```

Run Performance Tests:

```bash
k6 run performance/k6/smoke/api-health-smoke.js
```

Generate Allure Report:

```bash
allure serve build/allure-results
```

---

## Documentation

### New Joiners

Start here:

```text
docs/START_HERE.md
```

### Architecture

```text
docs/FRAMEWORK_ARCHITECTURE.md
```

### Roadmap

```text
docs/ROADMAP.md
```

### Changelog

```text
docs/CHANGELOG.md
```

### Architecture Decisions

```text
docs/DECISIONS/
```

### AI Agents

```text
docs/agents/
```

---

## Current Version

```text
MAPAF v2.4
```

### Status

```text
Active Development
```

---

## Maintained By

**Aneesh Neladri**

Automation Manager | Test Architect | Quality Engineering Leader
