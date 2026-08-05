# Contributing to MAPAF

Thank you for contributing to MAPAF (Mobile API Performance Automation Framework).

This document describes the development workflow, coding standards, testing requirements, and contribution process followed by the framework.

---

# Before You Start

Before contributing, ensure you have:

* Java 17
* Gradle
* Appium 2.x
* Android SDK
* Xcode (macOS for iOS development)
* k6
* Allure
* BrowserStack account (if required)

Refer to the training labs for environment setup.

---

# Branch Strategy

Never commit directly to `main`.

Feature work should always be developed in a feature branch.

Example:

```bash
git checkout develop
git pull origin develop
git checkout -b feature/my-feature
```

Examples:

```text
feature/ai-provider-layer

feature/browserstack-ios

feature/healing-dashboard

fix/mobile-locator

hotfix/performance-threshold
```

---

# Development Workflow

1. Pull the latest `develop` branch.
2. Create a feature branch.
3. Implement the change.
4. Run required validation.
5. Update documentation if necessary.
6. Commit changes.
7. Push branch.
8. Open a Pull Request.
9. Address review comments.
10. Merge after approval.

---

# Coding Standards

General principles:

* Keep classes focused on a single responsibility.
* Prefer composition over duplication.
* Avoid hardcoded values.
* Use configuration through `ConfigManager`.
* Do not introduce `Thread.sleep()`.
* Use `WaitUtils` for synchronization.
* Prefer native Appium locator strategies over XPath where possible.

---

# Package Structure

New classes should follow the existing project structure.

Examples:

```text
api/
mobile/
core/
performance/
docs/
```

Avoid creating new top-level packages unless approved.

---

# Testing Requirements

Before opening a Pull Request, verify:

```bash
./gradlew clean compileTestJava
```

Run the relevant tests for your change:

```bash
./gradlew apiTest

./gradlew aiTest

./gradlew mobileTest

./gradlew iosTest
```

For performance changes:

```bash
k6 run performance/k6/smoke/api-health-smoke.js
```

---

# Documentation Requirements

Update documentation whenever you introduce:

* New feature
* New workflow
* New AI capability
* New training content
* New configuration property

Potential files to update:

```text
README.md

CHANGELOG.md

docs/

training/

architecture/

release notes
```

---

# Commit Message Convention

Use concise, descriptive commit messages.

Examples:

```text
Add BrowserStack iOS capability support

Improve mobile workflow execution

Introduce AI provider abstraction

Document AI provider architecture
```

Avoid generic messages such as:

```text
fix

update

changes

test
```

---

# Pull Request Checklist

Before requesting review:

* Code compiles successfully
* Relevant tests pass
* Documentation updated
* Changelog updated (if applicable)
* No unnecessary debug code
* No hardcoded credentials
* New configuration documented

---

# Review Expectations

Reviewers will check:

* Architecture consistency
* Naming conventions
* Test coverage
* Documentation
* Performance impact
* AI integration (if applicable)

---

# Release Process

Releases follow semantic versioning.

Example:

```text
v1.4.0-ai-platform-foundation
```

Release process:

1. Merge into `develop`
2. Validate framework
3. Update release notes
4. Update changelog
5. Create Git tag
6. Publish release

---

# Areas of the Framework

MAPAF consists of:

* API Automation
* Mobile Automation
* Performance Testing
* AI Platform
* Training Platform
* CI/CD
* Grafana Observability

Contributors should understand the architecture before making cross-layer changes.

---

# Getting Help

If you are unsure about an implementation:

* Review the architecture documentation.
* Review the training labs.
* Review previous implementations.
* Discuss significant architectural changes before implementation.

The goal is to keep MAPAF consistent, maintainable, and easy to extend.

