# Lab 13 — Playwright Enterprise Testing

## Objective

Execute and understand MAPAF's Playwright capabilities, including sequential, parallel, selected-browser, and cross-browser testing.

## Prerequisites

```bash
java -version
./gradlew clean compileTestJava
./gradlew playwrightInstall
```

## Execution modes

Standard suite:

```bash
./gradlew webTest -Pheadless=true
```

Parallel suite:

```bash
./gradlew webParallelTest -PwebThreads=4 -Pheadless=true
```

Cross-browser suite:

```bash
./gradlew webCrossBrowserTest -PwebThreads=4 -Pheadless=true
```

Selected browsers:

```bash
./gradlew webSelectedBrowserTest -Pbrowser=CHROMIUM,FIREFOX -Pheadless=true
```

Retry support:

```bash
./gradlew webCrossBrowserTest -PretryCount=2 -Pheadless=true
```

## Verification

Confirm that:

- Each selected browser task executes.
- Parallel tests do not share page or context state.
- Retry output appears only for eligible failures.
- A successful retry is not mistaken for a first-attempt pass.
- Browser-specific result files are generated.

## Challenge

Run only WebKit, generate its summary, and explain how the dashboard should represent the two browsers that were not selected.

## Completion criteria

The trainee can choose the correct Gradle task and parameters for local execution, regression, CI/CD, and demonstrations.
