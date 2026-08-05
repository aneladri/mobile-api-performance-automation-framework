# ADR-014: Make execution, correlation and trace identity mandatory

**Status: Accepted**

## Decision
Every platform execution creates and propagates an execution ID, correlation ID and trace ID through events and outcomes.

## Consequences
Cross-module observability and later OpenTelemetry integration have a stable identity model.
