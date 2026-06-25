# MAPAF Release Notes

---

# v1.4.0 – AI Platform Foundation

Release Date: June 2026

## Overview

This release introduces the foundation of the MAPAF AI Platform.

The framework now includes a provider abstraction layer, enabling future integration with multiple Large Language Models (LLMs) without changing framework components.

---

## New Features

### AI Provider Architecture

Added:

* AIProvider
* AIProviderFactory
* AIRequest
* AIResponse
* ClaudeAIProvider
* HealingAIService

Documentation:

```
docs/agents/AI_PROVIDER_ARCHITECTURE.md
```

---

### Mobile Framework Improvements

Implemented:

* HealingBaseScreen integration
* Native Appium accessibility locator strategy
* BrowserStack iOS capability support
* Screenshot capture only on failures

---

### Performance Framework

Added:

* Shared k6 configuration
* Shared threshold configuration
* Authentication setup for performance tests
* Grafana integration
* InfluxDB metrics
* Soak testing
* Performance observability

---

### Training Platform

Added:

* Trainer Guide
* Training Plan
* AI Healing Exercise (Lab 07b)
* Beginner curriculum improvements

---

### CI/CD

Improved:

* Mobile workflow
* Performance workflow
* Removed duplicate workflow
* BrowserStack improvements

---

### Architecture

Added:

* AI Provider Layer
* HealingAIService
* Provider abstraction
* Legacy OrchestratorRuntime deprecation

---

## Improvements

* Cleaner mobile architecture
* Better AI extensibility
* Faster locator strategy
* Reduced screenshot storage
* Improved BrowserStack support
* Simplified workflow structure

---

## Known Limitations

Current AI Platform provides the foundation only.

Future releases will include:

* Claude locator healing integration
* Additional AI providers
* AI dashboard
* Performance baseline management

---

# Previous Releases

## v1.3.0

Feedback remediation release.

## v1.2.0

Performance observability.

## v1.1.0

Locator healing foundation.
