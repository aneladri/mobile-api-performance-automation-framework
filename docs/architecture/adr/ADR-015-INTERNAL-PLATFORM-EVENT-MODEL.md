# ADR-015: Use an internal event model with pluggable publishers

**Status: Accepted**

## Decision
The execution kernel emits immutable lifecycle events through a publisher port. Sprint 0.2 provides a thread-safe in-memory implementation.

## Consequences
Reporting, risk, AI and integrations can subscribe without coupling to module internals. External buses remain a v3.0 adapter concern.
