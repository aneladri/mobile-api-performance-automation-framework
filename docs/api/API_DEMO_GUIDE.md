# API Demo Guide

## Purpose

The API demo proves that the framework can load payloads, execute REST calls, validate response fields and schemas, exercise authentication helpers, and validate expected error responses.

## Demo target

The default QA configuration uses `https://httpbin.org`. Override it at runtime with:

```bash
./gradlew apiTest -DbaseUrl=https://your-api.example
```

## Included tests

- `HealthCheckTest` — confirms the API layer can reach the configured target.
- `PayloadManagerTest` — confirms payload fixtures load correctly.
- `UserPayloadApiDemoTest` — POST request, response assertions, schema validation, and Allure request/response evidence.
- `AuthenticationApiTest` — Basic authentication and API-key header transmission.
- `ErrorResponseApiTest` — expected 4xx and 5xx behavior.

## Run locally

```bash
./gradlew clean apiTest
```

## Validation gates

1. All API tests pass together.
2. `build/allure-results` contains request and response attachments for the demo test.
3. The GitHub Actions `API Tests` workflow is green after merge.

## Notes

The public demo endpoint is suitable for framework validation only. Replace it with a controlled internal mock or client API for stable project regression coverage.
