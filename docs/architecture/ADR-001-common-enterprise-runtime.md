# ADR-001: Common Enterprise Runtime

## Status
Accepted for MAPAF Enterprise v2.3.

## Decision
API, Performance, Mobile, Playwright, AI Generation and Healing enterprise demos will share a common execution identity, evidence registry, quality-gate model, sensitive-data masking policy and reporting contract.

## Rationale
The existing quick demos remain stable, while enterprise demos gain consistent governance, diagnostics and dashboard-ready output. Existing module-specific result models are adapted into `common.reporting.model.ExecutionSummary` instead of being replaced.

## Consequences
- Existing quick-demo Gradle tasks remain unchanged.
- API and Performance retain their current enterprise result models.
- New modules should publish through the common `ExecutionSummary` contract.
- AI recommendations require human approval and cannot silently modify permanent automation code.
