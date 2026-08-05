# ADR-018 — MAPAF Platform Intelligence History

## Status
Accepted for MAPAF v3.0 Sprint 4.1.

## Decision
MAPAF will persist immutable Doctor history records after each Doctor assessment.

## Contract
- Record: `mapaf.intelligence.history/v1`
- Index: `mapaf.intelligence.history-index/v1`

## Design principles
- The Doctor engine remains unchanged.
- History is a post-assessment concern.
- Records are immutable and content-addressed.
- Duplicate executions do not create duplicate files.
- Retention is configurable with `mapaf.intelligence.history.retention`.
- File-system storage is the initial implementation behind an interface.
