# Lab 03 – API Testing

## Goal

By the end of this lab, you will understand:

* What an API is
* Why API testing is important
* How MAPAF executes API tests
* How to create a new API test
* How responses are validated
* How API failures are investigated

---

# What is an API?

API stands for:

```text id="api1"
Application Programming Interface
```

An API allows two systems to communicate.

Example:

```text id="api2"
Mobile App
↓
API
↓
Backend System
```

When a user logs into a mobile application:

```text id="api3"
Username
Password
↓
API Request
↓
Backend Validation
↓
Response
```

The mobile application does not directly access the database.

It uses APIs.

---

# Why Test APIs?

API tests are:

```text id="api4"
Faster
More Stable
Less Expensive
```

than UI tests.

Example:

```text id="api5"
API Test
≈ 1 second

UI Test
≈ 20 seconds
```

API testing helps identify:

* Backend failures
* Authentication failures
* Contract violations
* Data issues

before UI tests run.

---

# API Layer Architecture

```text id="api6"
HealthCheckTest
↓
BaseApiClient
↓
ApiEndpoints
↓
RestAssured
↓
API
```

---

# Key Components

## BaseApiClient

### What?

Provides reusable API request configuration.

### Why?

Avoids duplicate code.

### Example

```java id="api7"
BaseApiClient.request()
```

---

## ApiEndpoints

### What?

Stores endpoint definitions.

### Example

```java id="api8"
ApiEndpoints.STATUS_200
```

---

## ResponseValidator

### What?

Validates responses.

### Example

```java id="api9"
ResponseValidator.validateStatusCode(
        response,
        200
);
```

---

# First API Test

Open:

```text id="api10"
src/test/java/api/tests/HealthCheckTest.java
```

Example:

```java id="api11"
@Test
public void verifyFrameworkApiLayerRuns() {

    Response response =
            BaseApiClient.request()
                    .when()
                    .get(ApiEndpoints.STATUS_200);

    ResponseValidator.validateStatusCode(
            response,
            200
    );
}
```

---

# Understanding the Flow

Step 1

```java id="api12"
BaseApiClient.request()
```

Creates a configured request.

---

Step 2

```java id="api13"
.get(ApiEndpoints.STATUS_200)
```

Calls the endpoint.

---

Step 3

```java id="api14"
ResponseValidator.validateStatusCode()
```

Verifies success.

---

# Execute API Tests

Run:

```bash id="api15"
gradle apiTest
```

Expected:

```text id="api16"
BUILD SUCCESSFUL
```

---

# Create Your First API Test

Create:

```text id="api17"
src/test/java/api/tests/UserApiTest.java
```

Example:

```java id="api18"
@Test
public void verifyUserEndpoint() {

    Response response =
            BaseApiClient.request()
                    .when()
                    .get("/users");

    ResponseValidator.validateStatusCode(
            response,
            200
    );
}
```

---

# Common Mistakes

## Mistake

Hardcoding URLs.

Wrong:

```java id="api19"
.get("https://qa.company.com/users")
```

Correct:

```java id="api20"
.get(ApiEndpoints.USERS)
```

---

## Mistake

Skipping validation.

Wrong:

```java id="api21"
response = request.get(...)
```

Correct:

```java id="api22"
ResponseValidator.validateStatusCode(
        response,
        200
);
```

---

# Troubleshooting

## Error

```text id="api23"
401 Unauthorized
```

Check:

```text id="api24"
AuthManager
Token
Credentials
```

---

## Error

```text id="api25"
404 Not Found
```

Check:

```text id="api26"
ApiEndpoints
Base URL
Environment
```

---

## Error

```text id="api27"
Connection Refused
```

Check:

```text id="api28"
Backend Availability
VPN
Network
```

---

# How AI Helps

Future Flow:

```text id="api29"
API Failure
↓
FailureAnalysisAgent
↓
Recommendation
↓
Report
```

Example:

```text id="api30"
SSLHandshakeException
↓
SSL Configuration Failure
↓
Import certificate
```

---

# Checkpoint

The trainee should be able to answer:

1. What is an API?
2. Why do we test APIs?
3. What does BaseApiClient do?
4. What does ResponseValidator do?
5. How do you execute API tests?
6. How do you create a new API test?

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
