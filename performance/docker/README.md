# Docker Performance Demo Mode

## Design overview

Docker Compose runs the mock API, k6, and JMeter using pinned images and writes results back to the repository.

```text
mock-api  --> port 8089
k6        --> k6 summary under performance/results/k6
jmeter    --> JTL and HTML under performance/jmeter
```

## Setup

```bash
docker version
docker compose version
```

## Demo steps

Smoke:

```bash
./gradlew performanceDemoSmokeDocker
```

Load:

```bash
./gradlew performanceDemoLoadDocker
```

Start only the target:

```bash
./gradlew performanceDockerUp
curl http://localhost:8089/health
```

Stop:

```bash
./gradlew performanceDockerDown
```

## Extension guide

- Pin tool-image versions.
- Use `mock-api:8089` inside the Compose network.
- Mount new scripts/plans under `/workspace`.
- Write outputs to existing host result directories.
- Validate direct Docker Compose commands before adding Gradle tasks.

## Troubleshooting

```bash
docker compose -p mapaf-performance-demo \
  -f performance/docker/docker-compose.yml ps

docker compose -p mapaf-performance-demo \
  -f performance/docker/docker-compose.yml logs mock-api
```

If host port 8089 is busy:

```bash
PERFORMANCE_API_PORT=8090 ./gradlew performanceDockerUp
```

## Best practices

- Prefer Docker mode for CI and shared demonstrations.
- Run smoke before load.
- Archive JTL, HTML, k6 summary, and target metrics.
- Do not use a developer laptop as a production benchmark host.
