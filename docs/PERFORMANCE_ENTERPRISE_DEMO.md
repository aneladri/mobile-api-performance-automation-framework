# MAPAF Enterprise Performance Demo

The existing `performanceFrameworkDemo` remains unchanged.

## New command

```bash
./gradlew clean compileTestJava
./gradlew performanceEnterpriseDemo
```

The task runs the existing k6 and JMeter smoke workloads, then reads:

- `performance/results/k6/smoke-summary.json`
- `performance/jmeter/results/smoke.jtl`

It publishes:

- rich per-engine metrics
- unified latency and throughput metrics
- availability and error-rate quality gates
- rule-based bottleneck diagnostics
- `performance/reports/enterprise-summary.json`

## Prerequisites

- Node.js
- k6
- JMeter, configured through PATH, `JMETER_HOME`, or `JMETER_BIN`

## Quality gates

- Availability >= 99%
- P95 < 500 ms
- Error rate < 1%
