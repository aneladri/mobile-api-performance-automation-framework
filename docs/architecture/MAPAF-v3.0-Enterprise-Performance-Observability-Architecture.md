# MAPAF v3.0 Enterprise Performance Observability Architecture

## Purpose

This increment connects the governed MAPAF k6 execution platform to an enterprise observability stack without replacing the existing normalized HTML evidence.

## Runtime flow

```text
MAPAF Gradle Task
  -> Governed k6 Workload
  -> Prometheus Remote Write
  -> Prometheus
  -> Grafana Dashboard
  -> MAPAF Normalized Report
  -> Unified Release Readiness

Application / Agent Telemetry
  -> OTLP
  -> OpenTelemetry Collector
  -> Prometheus-compatible metrics and governed trace export
```

## Components

- **k6** remains the default performance execution engine.
- **Prometheus** receives k6 metrics using remote write and scrapes OpenTelemetry metrics.
- **Grafana** provides live business-transaction and engineering views.
- **OpenTelemetry Collector** provides the vendor-neutral ingestion point for future MAPAF, application, agent, and pipeline telemetry.
- **MAPAF normalized report** remains the durable execution evidence used by quality gates and the Unified Dashboard.

## Governance

Observability is opt-in and locally isolated. No production credentials are embedded. All ports, Grafana credentials, and endpoints can be overridden through environment variables. Existing non-observed performance tasks remain unchanged.
