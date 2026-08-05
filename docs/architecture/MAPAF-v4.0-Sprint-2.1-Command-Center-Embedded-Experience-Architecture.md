# MAPAF v4.0 Sprint 2.1 Command Center Embedded Experience Architecture

## Decision
The Command Center remains the persistent product shell. Internal MAPAF reports are displayed inside a governed module frame. Live third-party tools that commonly deny framing, including Grafana and Prometheus, retain safe new-tab navigation.

## Flow
Module registry -> runtime discovery -> freshness classification -> unified execution context -> SPA route -> embedded report or external-tool fallback.

## Governance
- Internal modules do not hardcode knowledge of other modules.
- Module routes are resolved by the registry.
- Missing evidence disables navigation and displays a recovery command.
- Evidence older than the configured threshold is classified as STALE.
- External tools use explicit `embed: false` policy.
