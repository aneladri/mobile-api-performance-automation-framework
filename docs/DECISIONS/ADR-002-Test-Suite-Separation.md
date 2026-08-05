# ADR-002 Test Suite Separation

## Status

Accepted

## Decision

Test suites are separated into:

* apiTest
* mobileTest
* iosTest
* regressionTest

Each suite has its own TestNG configuration and Gradle task.

## Benefits

* Independent execution
* Faster feedback
* Better CI/CD orchestration

