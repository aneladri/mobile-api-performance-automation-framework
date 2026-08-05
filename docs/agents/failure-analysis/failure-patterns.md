# Failure Patterns Library

## Purpose

This document contains known failure patterns encountered during MAPAF development.

The Claude Failure Analysis Agent should use these patterns when analyzing future failures.

---

# Pattern 001

## Pattern Name

ANDROID_HOME Missing

### Failure Type

Environment Configuration

### Error Signature

```text
SessionNotCreatedException

Neither ANDROID_HOME nor ANDROID_SDK_ROOT environment variable was exported
```

### Root Cause

Android SDK path not configured.

### Suggested Fix

```bash
export ANDROID_HOME=$HOME/Library/Android/sdk

export ANDROID_SDK_ROOT=$ANDROID_HOME
```

### Confidence

98%

### Owner

Automation Engineer

---

# Pattern 002

## Pattern Name

BrowserStack Credits Exhausted

### Failure Type

BrowserStack Session Failure

### Error Signature

```text
BROWSERSTACK_TESTING_TIME_LIMIT_EXHAUSTED
```

### Root Cause

BrowserStack account has no remaining execution minutes.

### Suggested Fix

* Purchase BrowserStack credits
* Request trial extension
* Use corporate BrowserStack account

### Confidence

100%

### Owner

Automation Engineer

---

# Pattern 003

## Pattern Name

SSL Certificate Trust Failure

### Failure Type

SSL / Certificate Failure

### Error Signature

```text
SSLHandshakeException

PKIX path building failed

unable to find valid certification path to requested target
```

### Root Cause

Certificate chain not trusted by Java truststore.

### Suggested Fix

Import trusted certificates into Java cacerts store.

### Confidence

99%

### Owner

DevOps / Automation Engineer

---

# Pattern 004

## Pattern Name

Appium Session Creation Failure

### Failure Type

Appium Driver Failure

### Error Signature

```text
SessionNotCreatedException
```

### Root Cause

Invalid capabilities, app path, driver configuration, or device configuration.

### Suggested Fix

Validate:

* Device name
* Platform version
* App path
* Driver installation
* Appium server

### Confidence

80%

### Owner

Mobile Automation Engineer

---

# Pattern 005

## Pattern Name

Locator Not Found

### Failure Type

Locator Failure

### Error Signature

```text
NoSuchElementException
```

### Root Cause

Locator changed or element not present.

### Suggested Fix

Validate:

* Accessibility ID
* XPath
* Element visibility
* Page state

### Confidence

85%

### Owner

Mobile Automation Engineer

---

# Pattern 006

## Pattern Name

Wait Timeout

### Failure Type

Synchronization Failure

### Error Signature

```text
TimeoutException
```

### Root Cause

Element or page did not become available within expected timeframe.

### Suggested Fix

Review:

* Explicit waits
* Page load timing
* Backend latency
* Synchronization strategy

### Confidence

85%

### Owner

Automation Engineer

---

# Future Patterns

Add new patterns whenever:

* A new failure occurs
* A root cause is confirmed
* A fix is validated

This file should grow continuously as the framework evolves.

