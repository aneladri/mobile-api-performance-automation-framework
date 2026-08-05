# ADR-007: Adopt a Platform-First Product Architecture

- Status: Accepted
- Date: 2026-08-01

## Context
MAPAF has evolved beyond a single automation framework and now includes mobile, web, API, performance, dashboards, evidence, failure intelligence, and AI capabilities.

## Decision
MAPAF will be governed as an enterprise platform. Shared execution, evidence, telemetry, security, and extension concepts belong to Platform Core. Tool-specific logic belongs to capability adapters.

## Consequences
- Shared concepts require explicit contracts.
- New capabilities must integrate through platform boundaries.
- Short-term implementation effort increases, while long-term duplication and coupling decrease.
