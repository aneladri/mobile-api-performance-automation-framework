# ADR-006: Enterprise Command Center

## Decision
Build MAPAF v2.6 as an additive executive command-center layer over the existing dashboard framework.

## Context
Enterprise modules publish rich, module-specific `enterprise-summary.json` files. The existing dashboard consumes a common summary contract and does not understand every module-specific field.

## Approach
The v2.6 adapter reads each enterprise summary defensively, maps common business and technical fields into a dashboard view, marks missing modules as `NOT_RUN`, and publishes an executive HTML dashboard plus JSON summary.

## Consequences
- Existing dashboard and demos remain backward compatible.
- Missing reports never break dashboard generation.
- Executive and Engineering views are presented from the same page.
- Future modules can be added through another adapter entry without redesigning the renderer.
