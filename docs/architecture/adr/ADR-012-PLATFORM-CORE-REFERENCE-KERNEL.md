# ADR-012: Introduce a dependency-light Platform Core reference kernel

**Status: Accepted**

## Decision
Create a small JDK-only kernel for execution identity, capability resolution, lifecycle orchestration and event publication. Existing modules are not rewritten in Sprint 0.2.

## Consequences
MAPAF gains an executable architectural center while preserving current demos. Migration requires adapters and regression validation.
