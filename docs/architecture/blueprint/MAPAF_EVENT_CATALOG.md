# MAPAF Platform Event Catalog

## Execution lifecycle

| Event | Producer | Intended consumers |
|---|---|---|
| `EXECUTION_STARTED` | Execution engine | observability, audit, dashboard timeline |
| `CAPABILITY_STARTED` | Execution engine | progress UI, telemetry |
| `CAPABILITY_COMPLETED` | Execution engine | reporting, readiness, analytics |
| `CAPABILITY_FAILED` | Execution engine | failure intelligence, alerting |
| `EXECUTION_COMPLETED` | Execution engine | reporting, release readiness |

## Planned extension events

`STEP_STARTED`, `STEP_COMPLETED`, `EVIDENCE_CAPTURED`, `FAILURE_DETECTED`, `HEALING_STARTED`, `HEALING_SUCCEEDED`, `RISK_CALCULATED`, `DASHBOARD_PUBLISHED`, and `RELEASE_APPROVED` are reserved in the governed vocabulary. Their payload contracts will be introduced by the owning bounded-context ADRs.

## Event rules

- Events are facts and are never updated after publication.
- Event names use past tense or explicit lifecycle state.
- Sensitive payloads, credentials and raw authorization headers are prohibited.
- Every event must carry execution, correlation, trace and capability identifiers.
- Consumers must be idempotent when connected to an external bus.
- Schema evolution must be backward compatible within a major platform version.
