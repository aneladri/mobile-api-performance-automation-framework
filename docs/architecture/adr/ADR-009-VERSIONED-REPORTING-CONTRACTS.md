# ADR-009: Version Enterprise Reporting Contracts

- Status: Accepted
- Date: 2026-08-01

## Decision
Enterprise summaries will evolve through explicit schema versions. v2.7 outputs remain supported. New fields are additive until adapters and migration tests are available.

## Rationale
Versioned contracts prevent dashboards, integrations, and historical reports from breaking when new capabilities are introduced.
