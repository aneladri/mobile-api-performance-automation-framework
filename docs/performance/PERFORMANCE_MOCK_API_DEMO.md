# Performance Mock API and Demo Guide

## Why a separate mock API is required

The TestNG API suite uses an embedded WireMock server with a dynamic port. That is ideal for deterministic functional tests, but it stops when the test JVM exits and cannot act as a sustained performance target.

The performance mock API is therefore a standalone service on port `8089`.

## Demo flow

Terminal 1:

```bash
node performance/mock-api/server.js
```

Terminal 2:

```bash
curl http://localhost:8089/health
curl -X POST http://localhost:8089/api/users \
  -H 'Content-Type: application/json' \
  -d '{"firstName":"Aneesh","lastName":"Neladri","email":"aneesh.demo@example.com","role":"QA"}'
```

Performance smoke:

```bash
k6 run performance/k6/smoke/api-smoke.js
```

Load:

```bash
k6 run performance/k6/load/api-load.js
```

Stress:

```bash
k6 run performance/k6/stress/api-stress.js
```

Spike:

```bash
k6 run performance/k6/spike/api-spike.js
```

Short soak demo:

```bash
SOAK_DURATION=3m k6 run performance/k6/soak/api-soak.js
```

## Controlled degradation demo

Start with baseline latency:

```bash
DEFAULT_DELAY_MS=100 node performance/mock-api/server.js
```

Or inject per-request delay and failure rate:

```bash
BASE_URL='http://localhost:8089' k6 run performance/k6/load/api-load.js
curl 'http://localhost:8089/api/users/1?delayMs=1500&errorRate=0.10'
```

Use `GET /metrics` to show request count, average latency, error rate and per-route statistics during or after the demo.

## Scope and limitation

This mock is intended to validate the framework, scripts, thresholds, dashboards and demo flows. It does not predict the performance capacity of a real client service. Final performance conclusions must be made against an environment that represents the production architecture.
