# Install MAPAF Architecture Sprint 0.2

Extract this ZIP over the repository root, then run:

```bash
bash scripts/architecture/install-platform-core-sprint-0.2.sh
./gradlew clean compileTestJava
./gradlew platformCoreGate
```

The installer only adds one idempotent `apply from` line to `build.gradle`. It does not replace the existing build file.
