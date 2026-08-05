# MAPAF v2.9 Sprint 3.3.4

## Performance Health Probe

### Delivered

- k6 runtime validation
- JMeter runtime validation
- Performance enterprise-summary validation
- Performance dashboard validation
- Performance failure-showcase validation
- Production workload evidence validation
- Request-volume validation
- Error-rate threshold validation
- P95 latency threshold validation
- Workload-status validation
- Performance Health unit tests
- MAPAF Doctor publisher integration

### Threshold configuration

Maximum error rate:

`-Dmapaf.performance.health.max.error.rate.percent=1.0`

Maximum P95 latency:

`-Dmapaf.performance.health.max.p95.ms=1000`

### Status behavior

- Passing workload, tools, reports, and production evidence: `HEALTHY`
- Passing SLAs with missing tooling or supporting evidence: `DEGRADED`
- Missing enterprise summary, failed workload, excessive error rate,
  or excessive P95 latency: `UNHEALTHY`
