# MAPAF Enterprise v2.5 - Mobile Enterprise Demo

## Prerequisites
- Appium server running
- Android emulator or physical device connected
- Existing mobile configuration valid
- Demo application installed

## Validation
```bash
./gradlew clean compileTestJava
./gradlew mobileEnterpriseUnitTest
```

## Live demo
```bash
./gradlew mobileEnterpriseDemo \
  -Dmapaf.demo.pacing.enabled=true \
  -Dmapaf.demo.pacing.millis=2000
```

## Fast CI mode
```bash
./gradlew mobileEnterpriseDemo \
  -Dmapaf.demo.pacing.enabled=false
```

The `-D` options are JVM system properties and must be part of the Gradle command. They cannot be run as standalone shell commands.

## Outputs
- Allure evidence in `build/allure-results`
- Mobile dashboard summary in `mobile/reports/enterprise-summary.json`
- Screenshots attached to Allure

## Business workflow
1. Validate device and application readiness
2. Create scan session
3. Capture living-room evidence
4. Review capture quality
5. Upload RoomScan evidence
6. Complete AI processing
7. Validate business outcome
