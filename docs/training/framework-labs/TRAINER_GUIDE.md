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
