# Failure Analysis Classifications

## Purpose

This document defines the standard failure categories used by the Claude Failure Analysis Agent.

The agent should classify every failure into one primary category and optionally one secondary category.

---

## Classification Categories

### 1. Environment Configuration

Failures caused by missing or incorrect local/CI environment setup.

Examples:

- `ANDROID_HOME` missing
- `JAVA_HOME` incorrect
- Android SDK not installed
- Appium server not running
- Emulator not detected

---

### 2. Dependency Resolution

Failures caused by Gradle, Maven, npm, or package dependency issues.

Examples:

- Dependency download failure
- Missing transitive dependency
- Gradle cache corruption
- Plugin resolution failure

---

### 3. SSL / Certificate Failure

Failures caused by Java truststore or corporate SSL inspection.

Examples:

- `SSLHandshakeException`
- `PKIX path building failed`
- `unable to find valid certification path`

---

### 4. Appium Driver Failure

Failures caused by Appium session creation or driver configuration.

Examples:

- `SessionNotCreatedException`
- UiAutomator2 driver missing
- XCUITest driver missing
- App path invalid
- Device capability mismatch

---

### 5. BrowserStack Session Failure

Failures caused by BrowserStack configuration or cloud execution setup.

Examples:

- Invalid credentials
- Invalid `bs://` app id
- Unsupported device
- Capability mismatch
- Session timeout

---

### 6. Locator Failure

Failures caused by invalid, stale, or changed locators.

Examples:

- `NoSuchElementException`
- `StaleElementReferenceException`
- Text changed
- Accessibility ID changed
- XPath no longer valid

---

### 7. Synchronization Failure

Failures caused by timing or wait issues.

Examples:

- Element not visible in time
- Page not loaded
- Animation delay
- Backend response delay
- Explicit wait timeout

---

### 8. API Authentication Failure

Failures caused by invalid or expired API authentication.

Examples:

- 401 Unauthorized
- 403 Forbidden
- Expired bearer token
- Invalid API key
- Missing auth header

---

### 9. API Test Data Failure

Failures caused by missing, invalid, or inconsistent test data.

Examples:

- Missing JSON payload
- Invalid schema
- Test user not found
- Duplicate test data
- Environment mismatch

---

### 10. Performance Threshold Failure

Failures caused by performance metrics exceeding thresholds.

Examples:

- p95 response time exceeded
- Error rate exceeded
- Throughput below expected value
- k6 threshold failure

---

### 11. CI/CD Failure

Failures caused by pipeline configuration or runner setup.

Examples:

- GitHub Actions YAML issue
- Missing secret
- Missing artifact path
- Permission denied
- Runner dependency missing

---

### 12. Infrastructure Failure

Failures outside framework/test control.

Examples:

- Service unavailable
- Network outage
- BrowserStack outage
- GitHub runner outage
- API gateway unavailable

---

## Output Format

The Claude Failure Analysis Agent should respond using this format:

```text
Failure Type:
Root Cause:
Evidence:
Suggested Fix:
Confidence Score:
Recommended Owner:
Recommended Next Action:
