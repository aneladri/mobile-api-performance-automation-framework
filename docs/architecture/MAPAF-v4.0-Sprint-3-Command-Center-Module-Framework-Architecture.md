# MAPAF v4.0 Sprint 3 — Command Center Module Framework Foundation

## Decision
The Command Center becomes a module host rather than a report launcher. Native module renderers consume governed module contracts through `/api/command-center/module` and render inside the persistent application shell. Existing HTML reports remain evidence sources and fallback views.

## Flow
Module Registry → Discovery Contract → Module API → Native Renderer → Command Center Shell → Source Evidence

## Native foundation modules
Performance Intelligence, Performance Trends, Release Readiness, MAPAF Doctor, and Mobile Evidence.

## Governance
Modules register through `MapafModuleRuntime`; no module directly depends on another module's filesystem path. Source reports remain reachable through the module definition and registry.
