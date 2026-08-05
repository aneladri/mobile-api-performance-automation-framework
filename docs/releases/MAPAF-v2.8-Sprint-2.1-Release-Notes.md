# MAPAF v2.8 Sprint 2.1

## Quality Gate Policy Foundation

### Delivered

- Governed gate severity model
- Standard gate status model
- Versioned quality-gate policy contract
- Auditable quality-gate result contract
- Release-blocking decision flag
- Gate evaluation context
- Gate evaluator interface
- Backward-compatible Release Readiness v2 fields
- Policy foundation unit tests
- Static architecture validation

### Severity model

- BLOCKER
- HIGH
- ADVISORY

### Status model

- PASS
- WARN
- FAIL
- NOT_RUN

### Compatibility

Sprint 1 release-readiness behavior remains unchanged.

The existing `mapaf.release-readiness/v1` report continues to generate.
Sprint 2 evaluators will begin publishing the v2 gate collection in
subsequent increments.
