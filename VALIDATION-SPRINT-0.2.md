# Validate MAPAF Architecture Sprint 0.2

```bash
./gradlew clean compileTestJava
./gradlew platformCoreTest
./gradlew validatePlatformCoreArchitecture
./gradlew platformCoreGate
```

Expected result: all architecture and runtime tests pass and the validation script reports `MAPAF Platform Core Sprint 0.2 validation passed.`
