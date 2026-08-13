# MAPAF M2.5 — Performance Evidence 2.0 Release Notes

## Added

- Versioned `mapaf.performance.evidence/v2` contract.
- Aggregated workload, throughput, error-rate and p50/p95/p99 evidence.
- Deterministic prior-run baseline comparison.
- Deterministic anomaly signals for threshold violations, material regressions and error-rate risk.
- SHA-256 integrity hash and execution provenance.
- Bounded local persistence under `reports/command-center/performance-evidence/`.
- Performance evidence collection/detail Command Center APIs.
- Performance Evidence Explorer with Metrics, Baseline, Anomalies, Thresholds and Provenance views.
- `performanceEvidence20ContractTest` and `mapafPerformanceEvidence20Gate` verification tasks.

## Performance

Evidence generation occurs after performance execution from aggregated results. The Quality Event Fabric blocking-overhead gate remains the release safety check and must stay below 2%.

## Compatibility

Existing performance execution responses remain compatible; they now include a compact Performance Evidence 2.0 reference after completed evidence generation.
