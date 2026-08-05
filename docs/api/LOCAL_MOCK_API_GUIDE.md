# Local Mock API Guide

## Purpose

The API suite uses an embedded WireMock server by default. This removes public-network,
proxy, rate-limit, and external-service availability dependencies from local and CI runs.

## Default execution

```bash
./gradlew clean apiTest
```

The suite will:

1. Start WireMock on a random free localhost port.
2. Set the runtime `baseUrl` to that server.
3. Register deterministic API stubs.
4. Run all API tests.
5. Stop the server after the suite.

## Supported mock endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/status/200` | Health check |
| GET | `/status/{400,404,500}` | Expected error validation |
| POST | `/post` | Echo JSON payload for demo/schema tests |
| GET | `/headers` | API-key header validation |
| GET | `/basic-auth/{user}/{password}` | Basic authentication validation |

## Use an external or real API

Override the local-mock flag and base URL:

```bash
./gradlew clean apiTest \
  -DuseLocalApiMock=false \
  -DbaseUrl=https://your-api.example.com
```

External execution is intentionally opt-in because it is less deterministic.

## Design notes

- The server uses a dynamic port to avoid conflicts.
- The lifecycle is controlled by `BaseApiTest` using TestNG suite hooks.
- The existing `BaseApiClient` continues to read the base URL through `ConfigManager`.
- Tests remain unchanged and can run against compatible real endpoints when needed.
