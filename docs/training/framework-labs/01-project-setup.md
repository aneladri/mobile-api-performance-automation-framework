# Lab 01 – Project Setup

## Goal

By the end of this lab, you will:

* Clone MAPAF
* Open the project
* Verify prerequisites
* Execute your first command
* Understand the project layout

---

# What is Project Setup?

Before creating automation tests, your machine must be able to:

* Build the framework
* Execute tests
* Run Appium
* Run k6
* Generate reports

Project setup is the process of preparing your local environment.

---

# Why Do We Need It?

Imagine writing a mobile test without:

* Java
* Appium
* Android SDK
* Xcode

The test cannot run.

The purpose of setup is to ensure your machine has everything MAPAF requires.

---

# MAPAF Technology Stack

| Technology | Purpose             |
| ---------- | ------------------- |
| Java       | Core language       |
| TestNG     | Test execution      |
| Appium     | Mobile automation   |
| Gradle     | Build tool          |
| Allure     | Reporting           |
| k6         | Performance testing |
| Git        | Source control      |

---

# Step 1 – Clone Repository

## What

Download the framework source code.

## Why

The framework lives in Git.

## How

```bash
git clone <repository-url>
```

Example:

```bash
git clone https://github.com/company/mapaf.git
```

---

# Step 2 – Open in VS Code

## What

Open the project.

## Why

All development happens inside the project workspace.

## How

```bash
cd automation-framework
code .
```

---

# Step 3 – Verify Java

## What

Check Java installation.

## Why

MAPAF is built using Java.

## How

```bash
java -version
```

Expected:

```text
Java 17+
```

---

# Step 4 – Verify Gradle

## What

Check Gradle installation.

## Why

Gradle compiles and executes the framework.

## How

```bash
gradle --version
```

Expected:

```text
Gradle installed
```

---

# Step 5 – Verify Appium

## What

Check Appium installation.

## Why

Android and iOS automation depend on Appium.

## How

```bash
appium --version
```

Expected:

```text
Appium 2.x
```

---

# Step 6 – Verify k6

## What

Check k6 installation.

## Why

MAPAF uses k6 for performance testing.

## How

```bash
k6 version
```

Expected:

```text
k6 installed
```

---

# Step 7 – Verify Project Builds

## What

Compile the framework.

## Why

Confirms dependencies and configuration are correct.

## How

```bash
gradle clean compileTestJava
```

Expected:

```text
BUILD SUCCESSFUL
```

---

# Step 8 – Explore Project Structure

## What

Understand where code lives.

## Why

Engineers must know where to create tests and framework components.

## Structure

```text
src/test/java
│
├── api
├── mobile
├── performance
├── core
└── ai
```

---

# Common Mistakes

## Error

```text
java: command not found
```

Fix:

Install Java 17.

---

## Error

```text
gradle: command not found
```

Fix:

Install Gradle.

---

## Error

```text
appium: command not found
```

Fix:

Install Appium.

---

# Troubleshooting

Always run:

```bash
git status
java -version
gradle --version
appium --version
```

before reporting setup issues.

---

# Checkpoint

The trainee should be able to answer:

1. What is MAPAF?
2. Why does MAPAF use Gradle?
3. Why is Appium required?
4. What command verifies the framework compiles?
5. Where do API tests live?

# Lab 01 — Project Setup

## Goal

Prepare the repository, validate the Java/API baseline, and configure one performance execution mode.

## Steps

```bash
cd ~/Documents/automation-framework-updated
./gradlew clean compileTestJava
./gradlew apiTest
```

### Configure Portable JMeter

```bash
export JMETER_HOME="$HOME/Tools/apache-jmeter-5.6.3"
export PATH="$JMETER_HOME/bin:$PATH"
bash performance/scripts/resolve-jmeter.sh
```

### Validate the performance target

```bash
bash performance/scripts/start-mock-api.sh
curl http://localhost:8089/health
bash performance/scripts/stop-mock-api.sh
```

### Optional Docker validation

```bash
./gradlew performanceDockerUp
curl http://localhost:8089/health
./gradlew performanceDockerDown
```

## Completion criteria

- API suite passes.
- JMeter resolver returns an executable in Portable mode, or Docker mode works.
- Mock API health returns 200.
