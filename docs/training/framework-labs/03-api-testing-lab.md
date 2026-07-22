# Lab 03 — API Testing and the Embedded Local Mock

## Goal

By the end of this lab, you will be able to:

- Explain the MAPAF API architecture.
- Run the complete API suite without relying on a public service.
- Create a REST Assured test.
- Validate status, content type, JSON paths, and schema.
- Exercise Basic Auth and API-key authentication.
- Validate expected 4xx and 5xx responses.
- Diagnose API failures using logs and Allure evidence.

## Prerequisites

```bash
java -version
./gradlew clean compileTestJava
```

## Design overview

```text
TestNG test
    ↓
BaseApiTest
    ↓
Embedded WireMock lifecycle
    ↓
BaseApiClient / AuthManager
    ↓
REST Assured request
    ↓
ResponseValidator / SchemaValidator
    ↓
Allure results
```

The embedded mock starts on a random local port before the suite and stops after the suite. This makes framework tests deterministic and suitable for CI.

## Key components

| Component | Responsibility |
|---|---|
| `BaseApiTest` | API test lifecycle and runtime base URL |
| `ApiMockServer` | Starts and stops WireMock |
| `ApiMockStubs` | Defines deterministic endpoints |
| `BaseApiClient` | Shared request specification |
| `AuthManager` | Bearer, Basic, and API-key authentication |
| `PayloadManager` | Loads JSON payloads |
| `ResponseValidator` | Reusable response assertions |
| `SchemaValidator` | JSON schema validation |

## Step 1 — Run the baseline

```bash
./gradlew clean apiTest
```

Expected tests include:

- Health check
- Payload loading
- User payload POST flow
- API-key authentication
- Basic authentication
- Expected 400, 404, and 500 responses
- Embedded mock availability

## Step 2 — Inspect the demo test

Open:

```text
src/test/java/api/tests/UserPayloadApiDemoTest.java
```

Trace this flow:

```text
Load sample-user.json
      ↓
POST /post
      ↓
Validate status and content type
      ↓
Validate echoed JSON
      ↓
Validate schema
```

## Step 3 — Inspect the local stubs

Open:

```text
src/test/java/api/mock/ApiMockStubs.java
```

Identify the stubs for:

- `/post`
- `/headers`
- `/basic-auth/{username}/{password}`
- `/status/{code}`

## Step 4 — Create a new deterministic test

Create:

```text
src/test/java/api/tests/ApiResponseContractTest.java
```

Exercise:

1. Call a local mock endpoint.
2. Validate status code.
3. Validate `application/json` before parsing JSON.
4. Validate at least one JSON path.
5. Add a meaningful failure message.

## Step 5 — Add an expected-error test

Call:

```text
/status/422
```

Add the corresponding WireMock stub first, then validate that 422 is the expected outcome.

## Validation checkpoint

The trainee must demonstrate:

- `./gradlew apiTest` passes.
- The mock server start/stop messages are visible.
- The new test is deterministic.
- A deliberately incorrect assertion produces a useful failure message.

## Extension guide

To add a new API capability:

1. Add a mock stub.
2. Add endpoint constants where appropriate.
3. Add payload and schema fixtures.
4. Add reusable validation before adding test-specific assertions.
5. Add positive and negative tests.
6. Update the API demo guide.

## Troubleshooting

### JSON parsing fails

Print or attach:

- Status code
- Content type
- Raw body

Do not call `jsonPath()` before validating JSON content type.

### Tests call a public URL

Confirm the embedded mock is enabled and check the suite log for the random local URL.

### Duplicate Allure listener warnings

The listener may be configured both globally and in individual tasks/suites. This warning is non-blocking, but the framework should keep one registration point.

## Best practices

- Prefer local deterministic services for framework tests.
- Keep contract schemas versioned.
- Do not log secrets or authorization tokens.
- Test expected failures explicitly.
- Attach request and response evidence only after masking sensitive values.
