# MAPAF Platform Contract Catalog

## Public contracts introduced in Sprint 0.2

| Contract | Purpose | Stability |
|---|---|---|
| `Capability` | common execution extension point | Experimental, migration-safe |
| `CapabilityDescriptor` | identity and semantic version metadata | Experimental |
| `CapabilityResult` | normalized capability result | Experimental |
| `CapabilityRegistry` | registration and discovery port | Experimental |
| `PlatformExecutionEngine` | orchestration port | Experimental |
| `PlatformEventPublisher` | event publication port | Experimental |
| `PlatformEventSubscriber` | event consumer port | Experimental |

## Contract policy

- Contracts depend on JDK types and other platform-core contracts only.
- Implementations may depend on module or vendor libraries; interfaces may not.
- Breaking changes require an ADR, migration notes and a major contract version.
- Maps in contract payloads are extension points, not substitutes for governed domain models.
- No contract may expose credentials, driver instances, browser pages or vendor SDK objects.
