# MAPAF v3.0 — k6 Performance Platform Foundation

## Added

- Governed k6 platform structure for business flows, workloads, thresholds, profiles, reporting, and tests.
- UPDR smoke, load, spike, and exact 10,000-request workloads.
- Normalized evidence contract: `mapaf.performance.execution-summary/v1`.
- Performance Doctor prerequisite checks.
- Gradle tasks for platform execution, validation, reporting, and quality gates.
- Deterministic unit and contract tests.

## Compatibility

- Existing `updrPerformanceDemo`, generic k6 scripts, JMeter scripts, and dashboards remain unchanged.
- JMeter remains an optional compatibility engine.

## Known limitations

- Prometheus, Grafana, and OpenTelemetry exporters are architecture extension points in this foundation release; production adapters are planned under Phase 3 Enterprise Integrations.
