# MAPAF Enterprise v2.7 Validation

## Included
- Full framework baseline supplied by the user
- Integrated Presentation Mode Gradle tasks
- FULL, ENGINEERING, and EXECUTIVE profiles
- Allure Evidence Center generation and background launch
- Dashboard refresh/opening
- Stage timing and presentation summary JSON

## Local validation commands

```bash
./gradlew clean compileTestJava
./gradlew executiveDashboardUnitTest
./gradlew presentationDemo \
  -PpresentationProfile=FULL \
  -Pbrowser=CHROMIUM \
  -Pheadless=false \
  -PpresentationPacingEnabled=true \
  -PpresentationPacingMillis=2000
```

## Packaging-environment limitation
The Gradle wrapper could not download Gradle 9.5.1 because outbound network access to services.gradle.org was unavailable. Shell syntax and archive integrity were validated; full Gradle validation must be run locally.
