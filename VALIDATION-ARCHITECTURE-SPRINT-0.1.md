# MAPAF Architecture Foundation Validation

Run:

```bash
./gradlew clean compileTestJava
./gradlew architectureGovernanceTest
./gradlew validateArchitectureFoundation
```

Expected:
- Java test sources compile.
- Architecture governance suite passes two tests.
- All required architecture and ADR files are reported as PASS.

Optional regression smoke:

```bash
./gradlew mobileEnterpriseUnitTest webEnterpriseUnitTest executiveDashboardUnitTest
```
