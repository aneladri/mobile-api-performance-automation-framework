# Shared Quality Experience Semantics

MAPAF and ZENIQ deliberately remain separate products:

- MAPAF explains **what happened during quality execution**.
- ZENIQ explains **what the evidence means for quality knowledge and decisions**.

They share a semantic status language so that the same state means the same thing across both experiences.

| Domain | States |
| --- | --- |
| Runtime | HEALTHY, DEGRADED, FAILED |
| Intelligence resolution | RESOLVED, PARTIALLY_RESOLVED, UNRESOLVED |
| Evidence freshness | FRESH, STALE, EXPIRED |
| Proof | PROVEN, PARTIALLY_PROVEN, UNPROVEN |
| Knowledge action | RECOMMENDED, STOP, BLOCKED |

The Command Center maps its product-specific palette into CSS variables prefixed `--quality-`. ZENIQ maps its own design tokens into the same semantic contract. Shared semantics do not require a shared deployment artifact or a common frontend framework.
