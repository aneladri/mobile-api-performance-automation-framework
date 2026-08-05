# MAPAF Standalone Performance Mock API

## Purpose

A deterministic Node.js target for k6 and JMeter demos. It is separate from the embedded WireMock server used by functional API tests.

## Portable setup and demo

```bash
bash performance/scripts/start-mock-api.sh
curl http://localhost:8089/health
curl http://localhost:8089/metrics
```

Stop:

```bash
bash performance/scripts/stop-mock-api.sh
```

## Docker setup

```bash
./gradlew performanceDockerUp
curl http://localhost:8089/health
./gradlew performanceDockerDown
```

## Useful endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/health` | Readiness |
| GET | `/metrics` | In-memory metrics |
| POST | `/admin/reset` | Reset state and metrics |
| GET/POST/PUT/DELETE | `/api/users...` | CRUD workflow |
| GET | `/payload?sizeKb=100` | Payload-size demo |
| GET | `/status/:code` | Error-path demo |
| POST | `/echo` | Echo request and headers |

## Latency and failure injection

```bash
curl "http://localhost:8089/health?delayMs=750"
curl "http://localhost:8089/api/users/1?errorRate=0.25"
```

## JMeter demo sequence

```bash
curl -X POST http://localhost:8089/admin/reset
./gradlew jmeterSmoke -PjmeterHome="$HOME/Tools/apache-jmeter-5.6.3"
open performance/jmeter/reports/smoke/index.html
curl http://localhost:8089/metrics
```

## Extension guide

1. Keep success behavior deterministic by default.
2. Validate inputs and bound payload/delay/error values.
3. Reuse common fault-injection and metrics logic.
4. Add curl examples.
5. Add corresponding k6 and JMeter coverage.
6. Update demo and training documentation.

## Troubleshooting

```bash
lsof -i :8089
cat performance/reports/mock-api/mock-api.log
bash performance/scripts/stop-mock-api.sh
```

## Best practices

- Use synthetic data only.
- Reset state between comparison runs.
- Do not use this service as a production benchmark.
- Keep default responses fast and deterministic.
