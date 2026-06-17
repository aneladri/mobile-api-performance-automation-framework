# MAPAF - Getting Started

## Overview

MAPAF (Mobile API Performance Automation Framework) supports:

* API Automation
* Mobile Automation
* Performance Testing
* BrowserStack Execution
* GitHub Actions
* Allure Reporting

---

# Prerequisites

## Common

Required:

* Git
* Java 17
* Gradle
* IDE (IntelliJ IDEA Recommended)

Verify:

```bash
java -version
gradle -version
git --version
```

---

# macOS Setup

## Install Homebrew

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

## Install Java 17

```bash
brew install openjdk@17
```

Verify:

```bash
java -version
```

## Install Gradle

```bash
brew install gradle
```

## Install Appium

```bash
npm install -g appium
```

Verify:

```bash
appium --version
```

## Install Android Platform Tools

```bash
brew install android-platform-tools
```

Verify:

```bash
adb version
```

## Install Android Studio

Download:

https://developer.android.com/studio

Install:

* Android SDK
* Android Emulator
* Android Platform Tools

---

# Windows Setup

## Install Java 17

Download:

https://adoptium.net/

Verify:

```powershell
java -version
```

## Install Gradle

Download:

https://gradle.org/install/

Verify:

```powershell
gradle -version
```

## Install Node.js

Download:

https://nodejs.org/

Verify:

```powershell
node -v
npm -v
```

## Install Appium

```powershell
npm install -g appium
```

Verify:

```powershell
appium --version
```

## Install Android Studio

Install:

* Android SDK
* Android Emulator
* Android Platform Tools

Verify:

```powershell
adb version
```

---

# Clone Repository

```bash
git clone <repository-url>
cd automation-framework
```

---

# Configure Environment

Update:

```text
src/test/resources/config/qa.properties
```

Example:

```properties
environment=qa

baseUrl=http://httpbin.org

execution=local

platform=android

appiumServerUrl=http://127.0.0.1:4723

androidDeviceName=emulator-5554
androidAppPath=apps/android/ApiDemos-release.apk
```

---

# Run API Tests

```bash
gradle clean apiTest
```

---

# Run Mobile Tests

Start Appium:

```bash
appium
```

Verify emulator:

```bash
adb devices
```

Run:

```bash
gradle clean mobileTest
```

---

# Run Performance Tests

Install k6:

macOS:

```bash
brew install k6
```

Windows:

https://k6.io/docs/get-started/installation/

Run:

```bash
k6 run performance/k6/smoke/api-health-smoke.js
```

---

# Generate Allure Report

Run tests:

```bash
gradle clean apiTest
```

Generate report:

```bash
allure serve build/allure-results
```

---

# GitHub Actions

Workflows:

* API Tests
* Mobile Tests
* Performance Tests
* BrowserStack Mobile Tests

View:

GitHub → Actions

---

# BrowserStack

Required Secrets:

* BROWSERSTACK_USERNAME
* BROWSERSTACK_ACCESS_KEY
* BROWSERSTACK_APP_ID

Execution:

```properties
execution=browserstack
```

---

# First Validation Checklist

API:

```bash
gradle clean apiTest
```

Expected:

```text
BUILD SUCCESSFUL
```

Mobile:

```bash
gradle clean mobileTest
```

Expected:

```text
verifyApiDemosLaunches PASSED
```

Performance:

```bash
k6 run performance/k6/smoke/api-health-smoke.js
```

Expected:

```text
0% failed requests
```

---

# Support

Documentation:

* FRAMEWORK_ARCHITECTURE.md
* ROADMAP.md
* CHANGELOG.md

Agent Documentation:

* CLAUDE_FAILURE_ANALYSIS_AGENT.md
* CLAUDE_DOCUMENTATION_AGENT.md
* CLAUDE_ARCHITECT_AGENT.md

