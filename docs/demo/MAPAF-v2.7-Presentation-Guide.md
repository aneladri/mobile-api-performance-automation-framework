# MAPAF Enterprise v2.7 Presentation Guide

## Prerequisites

- Run from the automation framework root directory.
- `./gradlew` is executable.
- Allure CLI is installed and available on `PATH`.
- Appium and the Android emulator are available for the Mobile stage.
- Playwright browsers are installed.
- k6 and JMeter are available for the Performance stage.

## Installation

1. Copy `performance/scripts/run-mapaf-presentation.sh` into the framework.
2. Append the contents of `gradle-snippets/v2.7-build.gradle.txt` to `build.gradle`.
3. Make the script executable:

```bash
chmod +x performance/scripts/run-mapaf-presentation.sh
```

## Full customer presentation

```bash
./gradlew presentationDemo \
  -PpresentationProfile=FULL \
  -Pbrowser=CHROMIUM \
  -Pheadless=false \
  -PpresentationPacingEnabled=true \
  -PpresentationPacingMillis=2000
```

## Engineering presentation

```bash
./gradlew presentationEngineeringDemo
```

## Executive presentation

```bash
./gradlew presentationExecutiveDemo
```

## Allure evidence showcase only

```bash
./gradlew allureShowcase
```

## Outputs

- `presentation/reports/presentation-summary.json`
- `dashboard/reports/index.html`
- `build/allure-results/`
- `build/allure-report/`

## Fast rehearsal

```bash
./gradlew presentationDemo \
  -PpresentationProfile=FULL \
  -Pheadless=true \
  -PpresentationPacingEnabled=false \
  -PpresentationOpenDashboard=false \
  -PpresentationOpenAllure=false
```
