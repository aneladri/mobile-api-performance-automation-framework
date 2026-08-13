# MAPAF M2.5 — Performance Evidence 2.0 Architecture

## Purpose

Performance Evidence 2.0 converts completed MAPAF performance runs into bounded, versioned, integrity-verifiable evidence without adding AI or persistence work to the load-generation hot path.

## Runtime boundary

```text
Load engine hot path
request -> response -> timing -> aggregate -> next request

After run completion
aggregated performance record
  -> Performance Evidence Builder
  -> deterministic baseline comparison
  -> deterministic anomaly signals
  -> bounded local evidence store
  -> Quality Event Fabric metadata event
  -> Command Center Performance Evidence Explorer
```

## Contract

`mapaf.performance.evidence/v2`

The contract contains run identity, workload shape, aggregate latency/throughput/error metrics, governed thresholds, deterministic baseline comparisons, anomaly signals, observability links, provenance and SHA-256 integrity.

## Baseline policy

The most recent prior persisted performance evidence artifact is used as the comparison baseline. A 5% deterministic comparison band classifies metrics as `REGRESSION`, `IMPROVEMENT`, or `STABLE`. No ML or LLM is used for this stage.

## Performance invariant

Performance Evidence 2.0 is built only after the run has completed and from aggregated results. It must not add inference, remote calls, or evidence persistence to each generated request.

## Intelligence boundary

Current anomaly signals are deterministic and marked `aiRequired: false`. Future statistical/ML processors may consume Performance Evidence 2.0 asynchronously through ZENIQ, but MAPAF remains the execution/evidence plane.
