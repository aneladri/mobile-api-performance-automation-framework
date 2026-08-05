# MAPAF Repository Strategy

## Current-State Rule
The existing Gradle repository remains the source of truth during v2.7-v2.9. A disruptive multi-module migration is deferred until contracts and ownership boundaries are stable.

## Target Logical Structure

```text
mapaf/
  platform-core/
  execution-runtime/
  evidence-reporting/
  capabilities/
    mobile/
    web/
    api/
    performance/
  intelligence/
    failure/
    readiness/
    doctor/
  integrations/
  ai-platform/
  sdk/
  samples/
  docs/
```

## Migration Principles
1. Logical boundaries precede physical repository movement.
2. New code must use approved package boundaries.
3. Cross-module dependencies must be explicit and one-directional.
4. Existing public tasks and report paths remain compatible during migration.
5. Each extraction requires an ADR and regression evidence.

## Package Direction for the Current Repository
- `core.enterprise.*`: platform contracts and shared runtime
- `mobile.enterprise.*`: mobile capability
- `web.enterprise.*`: web capability
- `api.enterprise.*`: API capability
- `performance.enterprise.*`: performance capability
- `dashboard.enterprise.*`: reporting projection
- `failure.showcase.*`: deterministic failure demonstration only

## Prohibited Patterns
- Direct dependencies from core to a capability
- Dashboard logic inside test classes
- Vendor-specific classes in core contracts
- Hard-coded absolute evidence paths
- Duplicate execution identities generated independently by each subsystem
