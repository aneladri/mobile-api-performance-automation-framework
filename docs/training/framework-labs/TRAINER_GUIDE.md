# MAPAF Trainer Guide
## Purpose

This guide helps trainers deliver the MAPAF onboarding programme consistently.

The objective is not to teach engineers how to memorize commands.

The objective is to teach engineers how MAPAF works, why it was designed that way, and how to contribute safely to the framework.

---

# Target Audience

This programme is designed for:

* New QA Engineers
* Automation Engineers
* SDETs
* Developers contributing automated tests
* Engineers migrating from another automation framework

No prior Appium experience is required.

No prior k6 experience is required.

No prior AI automation experience is required.

---

# Trainer Responsibilities

The trainer must:

* Guide the trainee through all labs
* Explain concepts before implementation
* Validate checkpoint questions
* Review exercises
* Conduct the capstone assessment

The trainer should avoid:

* Copying code for the trainee
* Solving exercises for the trainee
* Skipping checkpoints

---

# Training Philosophy

Every topic follows:

## What

What is this concept?

## Why

Why does MAPAF use it?

## How

How is it implemented?

## Exercise

How does the trainee apply it?

## Validation

How do we know they understood it?

---

# Daily Structure

Each day follows:

```text
Concept
↓
Demonstration
↓
Exercise
↓
Checkpoint
↓
Review
```

Recommended ratio:

```text
30% Theory

70% Hands-on
```

---

# Trainer Checklist

Before Day 1:

* Repository access verified
* Java installed
* Appium installed
* Android SDK installed
* k6 installed
* BrowserStack access verified
* Claude API key available (optional)

---

# Day 1

Focus:

Framework orientation.

Do not rush into code.

Validate understanding of:

* Framework architecture
* Project structure
* Core components

Checkpoint must be completed before moving forward.

---

# Day 2

Focus:

API testing.

Trainee must:

* Create endpoint
* Create API test
* Execute test
* Read report

---

# Day 3

Focus:

Android automation.

Trainee must:

* Create Screen Object
* Create Android test
* Execute Android test
* Debug locator failure

---

# Day 4

Focus:

iOS and BrowserStack.

Trainee must:

* Create iOS Screen Object
* Execute BrowserStack run
* Review session recording

---

# Day 5

Focus:

Performance and AI.

Trainee must:

* Execute Smoke
* Execute Load
* Execute Stress
* Review AI reports
* Complete Capstone

---

# Capstone Evaluation

The trainer evaluates:

| Area                | Weight |
| ------------------- | ------ |
| API Testing         | 15%    |
| Android Testing     | 15%    |
| iOS Testing         | 15%    |
| Performance Testing | 15%    |
| AI Analysis         | 15%    |
| Debugging           | 15%    |
| Git Workflow        | 10%    |

---

# Graduation Criteria

The trainee is considered MAPAF-ready when they can:

* Create tests independently
* Debug failures independently
* Interpret AI reports
* Use Git correctly
* Complete the Capstone Project

---

# Common Trainer Mistakes

## Mistake

Rushing through setup.

Result:

Environment issues later.

---

## Mistake

Skipping debugging exercises.

Result:

Engineers cannot investigate failures.

---

## Mistake

Teaching commands without architecture.

Result:

Engineers become copy-paste users.

---

# Success Criteria

The programme is successful when trainees can contribute production-quality automation without direct supervision.

## Performance module preparation

Before class:

1. Confirm whether learners use Portable or Docker mode.
2. For managed Macs, distribute the approved JMeter 5.6.3 archive or approved download instructions.
3. Verify `JMETER_HOME` and the resolver.
4. Run API, k6 smoke, and JMeter smoke.
5. Preserve fallback reports.

## Portable setup demonstration

```bash
mkdir -p "$HOME/Tools"
tar -xzf "$HOME/Downloads/apache-jmeter-5.6.3.tgz" -C "$HOME/Tools"
chmod +x "$HOME/Tools/apache-jmeter-5.6.3/bin/jmeter"
export JMETER_HOME="$HOME/Tools/apache-jmeter-5.6.3"
export PATH="$JMETER_HOME/bin:$PATH"
jmeter --version
```

## Teaching sequence

1. Explain functional versus performance mocks.
2. Start the target and show health/metrics.
3. Run k6 smoke.
4. Run JMeter smoke in non-GUI mode.
5. Open the dashboard and interpret APDEX/percentiles/throughput/errors.
6. Compare tool fit rather than declaring one universal winner.
7. Demonstrate one controlled delay or error-injection scenario.

## Common learner issues

- `JMETER_HOME` points to a missing directory.
- `bin/jmeter` is not executable.
- Gradle daemon did not receive updated environment variables; use `-PjmeterHome` or `./gradlew --stop`.
- Report directory already exists.
- Port 8089 is occupied.
- JMeter is incorrectly run in GUI mode for load.

## Assessment

Learners pass when they can:

- resolve JMeter without Homebrew changes;
- run a smoke profile;
- locate the JTL and HTML report;
- explain key metrics;
- extend one transaction;
- document setup and troubleshooting.
