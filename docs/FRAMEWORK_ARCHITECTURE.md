# MAPAF Framework Architecture

## Overview

MAPAF (Mobile API Performance Automation Framework) is a unified automation platform that supports:

* API Automation
* Mobile Automation
* Performance Testing
* Cloud Execution
* CI/CD Integration
* Reporting and Observability
* AI-Assisted Engineering

The framework is designed around separation of concerns, scalability, maintainability, and cloud-native execution.

---

# High-Level Architecture

MAPAF

├── Core Layer

├── API Layer

├── Mobile Layer

├── Performance Layer

├── Reporting Layer

├── CI/CD Layer

└── AI Agent Layer

---

# Core Layer

Purpose:

Provide shared framework capabilities.

Components:

* ConfigManager
* ExecutionConfig
* ExecutionMode
* DriverFactory
* DriverManager
* BaseTest
* BaseApiTest
* BaseMobileTest
* LoggerUtil
* TestListener

Responsibilities:

* Environment management
* Driver lifecycle
* Execution strategy
* Logging
* Framework lifecycle management

---

# API Automation Layer

Purpose:

Support REST API automation.

Components:

* BaseApiClient
* RequestBuilder
* AuthManager
* TokenManager
* ResponseValidator
* SchemaValidator
* PayloadManager
* JsonUtils

Capabilities:

* Authentication
* Token management
* Request construction
* Response validation
* JSON schema validation
* Test data management

Framework:

* REST Assured
* TestNG

---

# Mobile Automation Layer

Purpose:

Support Android and iOS automation.

Components:

* BaseScreen
* ScreenFactory
* LoginScreen
* LoginFlow
* WaitUtils
* GestureUtils
* LocatorBuilder
* LocatorType
* AndroidCapabilities
* IosCapabilities
* BrowserStackCapabilities
* ScreenshotUtils

Capabilities:

* Page Objects
* Business Flows
* Gestures
* Synchronization
* Capability management
* Screenshot capture

Framework:

* Appium 2
* TestNG

---

# Performance Layer

Purpose:

Support non-functional testing.

Components:

* K6 Smoke Tests
* K6 Load Tests
* K6 Stress Tests
* K6 Soak Tests
* Threshold Definitions

Capabilities:

* Latency validation
* Throughput validation
* Failure-rate monitoring
* Performance regression detection

Framework:

* K6

---

# Reporting Layer

Purpose:

Provide execution visibility.

Components:

* Allure
* Screenshots
* Framework Logs
* Gradle Reports

Capabilities:

* Test history
* Failure screenshots
* Execution analysis
* Artifact generation

---

# CI/CD Layer

Purpose:

Enable automated execution.

Components:

* API Tests Workflow
* Mobile Tests Workflow
* Performance Tests Workflow
* BrowserStack Workflow

Capabilities:

* Continuous testing
* Artifact publishing
* Cloud execution
* Automated validation

Platform:

* GitHub Actions

---

# Cloud Execution Layer

Supported Providers:

* BrowserStack
* Local Execution

Execution Modes:

* local
* browserstack

Future:

* Sauce Labs
* LambdaTest

---

# AI Agent Layer

Claude Failure Analysis Agent

Purpose:

Analyze failures and suggest root causes.

Claude Documentation Agent

Purpose:

Maintain framework documentation.

Claude Architect Agent

Purpose:

Review framework architecture and design decisions.

Future Agents:

* Self-Healing Agent
* Performance Analysis Agent
* CI/CD Agent
* Release Agent

---

# Design Principles

* Separation of concerns
* Reusability
* DRY
* Cloud readiness
* Environment independence
* CI/CD first
* AI-assisted maintenance

---

# Current Version

MAPAF v2.4
