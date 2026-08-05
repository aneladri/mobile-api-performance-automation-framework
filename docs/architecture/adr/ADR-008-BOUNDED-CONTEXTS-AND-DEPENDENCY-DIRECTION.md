# ADR-008: Define Bounded Contexts and Dependency Direction

- Status: Accepted
- Date: 2026-08-01

## Decision
MAPAF adopts bounded contexts for Platform Core, Automation Capabilities, Evidence and Reporting, Failure Intelligence, Release Readiness, Diagnostics, Enterprise Integrations, and AI Platform.

Dependencies point toward contracts in Platform Core. Core does not depend on capability implementations. Reporting consumes published summaries and evidence references.
