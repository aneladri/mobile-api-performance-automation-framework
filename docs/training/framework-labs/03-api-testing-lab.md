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

