# MAPAF v4.0 Performance Trend, Baseline, and Cardinality Governance Architecture

## Purpose

This increment extends the deterministic Performance Intelligence Agent with governed execution history, explicit baseline promotion, regression detection, and metric-tag cardinality controls.

## Flow

```text
k6 summaries -> Performance Intelligence -> History Capture
                                      -> Candidate Execution
Approved Baseline + Current Intelligence -> Trend Comparison -> Adjusted Release Decision
k6 source -> Cardinality Policy -> Tag Registry -> Performance Gate
```

## Governance

- A normal run never replaces the approved baseline.
- Only `READY`, zero-threshold-violation executions meeting minimum volume and score can be promoted.
- Correlation IDs remain HTTP headers but are prohibited as metric tags.
- Regression policy is versioned and deterministic.
- History records and baseline records use versioned contracts and preserve source execution identity.
