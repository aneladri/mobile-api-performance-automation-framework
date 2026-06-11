# Unified Test Automation Framework (UTAF)

## Project Information

| Property        | Value                                          |
| --------------- | ---------------------------------------------- |
| Framework Name  | Unified Test Automation Framework (UTAF)       |
| Version         | 1.0.0                                          |
| Framework Type  | Mobile, API & Performance Automation           |
| Created By      | YOUR NAME                                      |
| Role            | Senior QA Automation Engineer / Test Architect |
| Initial Release | June 2026                                      |
| Status          | Active Development                             |

---

# Framework Creator

## Author

**Name:** Aneesh Neladri

**Designation:** Automation Manager / Test Architect

### Expertise

* Mobile Test Automation
* API Test Automation
* Performance Testing
* Test Framework Architecture
* CI/CD Integration
* AI-Assisted Automation
* Quality Engineering

### Responsibilities

* Framework Architecture
* Automation Standards
* CI/CD Strategy
* Mobile Automation Design
* API Automation Design
* Performance Testing Strategy
* Framework Governance
* AI Automation Roadmap

---

# About This Framework

The Unified Test Automation Framework (UTAF) was designed and developed to provide a scalable enterprise-grade automation solution supporting:

* Android Mobile Automation
* iOS Mobile Automation
* API Automation
* Performance Testing
* CI/CD Integration
* Multi-Environment Execution
* Cloud Device Execution
* AI-Assisted Test Automation

The framework follows industry best practices and is built with maintainability, scalability, reusability, and extensibility as primary design principles.

---

# Technology Stack

| Layer                | Technology                |
| -------------------- | ------------------------- |
| Programming Language | Java 17                   |
| Build Tool           | Gradle                    |
| Test Framework       | TestNG                    |
| Mobile Automation    | Appium                    |
| Android Driver       | UiAutomator2              |
| iOS Driver           | XCUITest                  |
| API Automation       | REST Assured              |
| Performance Testing  | k6                        |
| Reporting            | Allure                    |
| CI/CD                | GitHub Actions / Jenkins  |
| Cloud Execution      | BrowserStack / Sauce Labs |
| Source Control       | Git                       |

---

# Framework Goals

The framework aims to:

* Reduce automation maintenance effort
* Support Android and iOS automation from a single framework
* Support API and UI automation under one platform
* Support performance testing integration
* Enable CI/CD execution
* Provide reusable automation components
* Support AI-assisted automation capabilities
* Improve release quality and speed

---

# Project Structure

```text
automation-framework/
│
├── README.md
├── build.gradle
├── settings.gradle
├── testng.xml
│
├── env/
│   ├── qa.properties
│   ├── staging.properties
│   └── uat.properties
│
├── apps/
│   ├── android/
│   └── ios/
│
├── performance/
│   └── k6/
│       ├── smoke-test.js
│       └── load-test.js
│
└── src/test/java/
    │
    ├── core/
    │   ├── config/
    │   ├── reporting/
    │   ├── utils/
    │   └── ai/
    │
    ├── api/
    │   ├── clients/
    │   ├── payloads/
    │   ├── schemas/
    │   └── tests/
    │
    └── mobile/
        ├── driver/
        ├── screens/
        ├── flows/
        └── tests/
```

---

# Environment Setup

## Java Installation

### macOS

Install Java 17:

```bash
brew install openjdk@17
```

Create Java symlink:

```bash
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk \
/Library/Java/JavaVirtualMachines/openjdk-17.jdk
```

Configure environment:

```bash
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc

echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc

source ~/.zshrc
```

Verify:

```bash
java -version
```

### Windows

Install Java 17 from:

```text
https://adoptium.net/
```

Set:

```text
JAVA_HOME=C:\Program Files\Java\jdk-17
```

Add:

```text
%JAVA_HOME%\bin
```

to the system PATH.

Verify:

```cmd
java -version
```

---

# Node.js Installation

### macOS

```bash
brew install node
```

### Windows

Download:

```text
https://nodejs.org
```

Verify:

```bash
node -v
npm -v
```

---

# Appium Installation

### macOS

If npm permissions are available:

```bash
npm install -g appium
```

If npm permission issues occur:

```bash
mkdir ~/.npm-global

npm config set prefix '~/.npm-global'

echo 'export PATH="$HOME/.npm-global/bin:$PATH"' >> ~/.zshrc

source ~/.zshrc

npm install -g appium
```

### Windows

Open Command Prompt as Administrator:

```cmd
npm install -g appium
```

Verify:

```bash
appium -v
```

---

# Appium Driver Installation

Install Android Driver:

```bash
appium driver install uiautomator2
```

Install iOS Driver:

```bash
appium driver install xcuitest
```

Verify:

```bash
appium driver list --installed
```

Expected:

```text
uiautomator2
xcuitest
```

---

# Starting Appium Server

```bash
appium
```

Expected:

```text
Appium REST http interface listener started
```

Verify server:

```bash
curl http://127.0.0.1:4723/status
```

Expected response:

```json
{
  "value": {
    "ready": true
  }
}
```

---

# Gradle Installation

### macOS

```bash
brew install gradle
```

Verify:

```bash
gradle -v
```

### Windows

Download:

```text
https://gradle.org/install/
```

Verify:

```cmd
gradle -v
```

---

# Running Tests

Execute:

```bash
gradle clean test
```

Expected:

```text
BUILD SUCCESSFUL
```

---

# SSL Certificate Issues

If you encounter errors such as:

```text
PKIX path building failed
SSLHandshakeException
certificate_unknown
```

Import macOS certificates:

```bash
security find-certificate -a -p \
/System/Library/Keychains/SystemRootCertificates.keychain \
> /tmp/macos-certs.pem
```

Import into Java:

```bash
sudo keytool -importcert \
-trustcacerts \
-noprompt \
-alias macos-root-certs \
-file /tmp/macos-certs.pem \
-keystore "$JAVA_HOME/lib/security/cacerts" \
-storepass changeit
```

Restart Gradle:

```bash
gradle --stop
```

Retry:

```bash
gradle clean test
```

---

# Performance Testing

Install k6.

### macOS

```bash
brew install k6
```

### Windows

```powershell
choco install k6
```

Run:

```bash
k6 run performance/k6/smoke-test.js
```

---

# Reporting

Planned reporting integrations:

* Allure Reports
* Screenshots
* Videos
* Device Logs
* Network Logs
* Test Evidence Attachments

Example:

```bash
allure serve build/allure-results
```

---

# Roadmap

## Phase 1

* Configuration Management
* Driver Factory
* API Client Framework
* Base Test Classes

## Phase 2

* Android Automation
* iOS Automation
* Page Object Model
* Business Flow Layer

## Phase 3

* REST Assured Integration
* Schema Validation
* Authentication Management

## Phase 4

* k6 Performance Framework
* Threshold Validation
* Performance Reporting

## Phase 5

* BrowserStack Integration
* Sauce Labs Integration

## Phase 6

* GitHub Actions Pipeline
* Jenkins Integration
* Release Quality Gates

## Phase 7

* AI Self-Healing
* Failure Classification
* Smart Locator Recovery
* Root Cause Analysis

---

# Daily Commands

Verify Java:

```bash
java -version
```

Verify Gradle:

```bash
gradle -v
```

Verify Appium:

```bash
appium -v
```

Start Appium:

```bash
appium
```

Run Tests:

```bash
gradle clean test
```

Run Performance Tests:

```bash
k6 run performance/k6/smoke-test.js
```

Stop Gradle Daemons:

```bash
gradle --stop
```

---

# Document History

| Version | Date      | Author    | Description                          |
| ------- | --------- | --------- | ------------------------------------ |
| 1.0.0   | June 2026 | YOUR NAME | Initial framework creation and setup |
| 1.1.0   | TBD       | YOUR NAME | Mobile automation implementation     |
| 1.2.0   | TBD       | YOUR NAME | API automation implementation        |
| 1.3.0   | TBD       | YOUR NAME | Performance testing implementation   |

---

## Maintained By

**Aneesh Neladri**

Automation Manager / Test Architect

Unified Test Automation Framework (UTAF)
