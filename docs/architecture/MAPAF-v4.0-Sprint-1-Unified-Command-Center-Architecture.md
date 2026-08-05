# MAPAF v4.0 Sprint 1 — Unified Command Center Architecture

The Command Center is an additive product shell over existing MAPAF evidence. A deterministic adapter reads available module contracts, normalizes status and score, and emits `mapaf.command-center/v1` JSON plus a standalone HTML experience. Missing evidence never breaks generation; modules render an explicit unavailable state and a remediation command.

Flow: module reports → tolerant adapters → unified model → executive decision → HTML/JSON Command Center.

Sprint 1 adapters: API, Mobile, Playwright, Performance, Doctor, and AI Intelligence. Evidence links remain relative and portable. External Grafana and Prometheus links are preserved for live observability.
