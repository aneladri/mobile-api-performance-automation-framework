# MAPAF Enterprise v2.3 Demo Guide

## Validate the release

```bash
./gradlew clean compileTestJava
./gradlew enterpriseRuntimeTest
```

## Existing quick demos

```bash
./gradlew mobileFrameworkDemo
./gradlew healingDemoTest
./gradlew apiFrameworkDemo
./gradlew performanceFrameworkDemo
./gradlew playwrightDemo
```

## Existing enterprise demos

```bash
./gradlew apiEnterpriseDemo
./gradlew performanceEnterpriseDemo
```

## Common runtime capabilities

- execution ID, correlation ID and trace ID
- shared evidence classification
- common quality-gate evaluation
- centralized masking of secrets, tokens, passwords and cookies
- adapters from API and Performance results to the common dashboard contract
- AI-agent governance defaults with human approval and non-blocking analysis
