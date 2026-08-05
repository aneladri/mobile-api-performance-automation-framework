# MAPAF Platform Core Migration Plan

## Strategy

Adopt a strangler migration. Existing demos and reports continue unchanged while adapters progressively implement platform contracts.

## Migration waves

1. **Wave 0 — Reference kernel:** contracts, registry, execution identity and local event bus.
2. **Wave 1 — Reporting adapter:** publish existing enterprise summaries from completion events without changing JSON contracts.
3. **Wave 2 — Capability adapters:** wrap Mobile, Web, API and Performance enterprise demos behind `Capability`.
4. **Wave 3 — Evidence adapter:** normalize screenshots, videos, trace, API payloads and performance reports.
5. **Wave 4 — Release Readiness:** consume normalized outcomes and events in v2.8.
6. **Wave 5 — MAPAF Doctor:** register health checks as governed capabilities in v2.9.
7. **Wave 6 — Enterprise integrations:** introduce external registry, event and observability adapters in v3.0.

## Compatibility rules

- No existing Gradle demo task is removed during Waves 0–3.
- Existing `enterprise-summary.json` fields remain readable.
- New platform-core code is additive until module adapters pass regression gates.
- Rollback consists of disabling adapter registration, not reverting module code.
