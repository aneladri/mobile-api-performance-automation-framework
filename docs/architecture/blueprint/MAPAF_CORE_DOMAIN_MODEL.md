# MAPAF Core Domain Model

## Aggregate boundaries

### Execution aggregate

- `ExecutionRequest`: immutable intent submitted to the platform.
- `ExecutionContext`: execution identity and trace correlation.
- `ExecutionOutcome`: final normalized result and duration.
- `ExecutionStatus`: lifecycle state vocabulary.

### Capability aggregate

- `Capability`: executable platform extension contract.
- `CapabilityDescriptor`: stable identity, display name, semantic version and type.
- `CapabilityResult`: normalized status, summary and structured outputs.
- `CapabilityRegistry`: discovery and duplicate-protection boundary.

### Event aggregate

- `PlatformEvent`: immutable lifecycle fact.
- `PlatformEventType`: governed event vocabulary.
- `PlatformEventPublisher`: event publication port.
- `PlatformEventSubscriber`: event consumption port.

## Future domain concepts

The following are deliberately modeled later in their owning bounded contexts: Evidence, BusinessTransaction, FailureIntelligence, RiskAssessment, QualityGate, ReleaseDecision, Agent, Skill, Plugin and Connector.

## Invariants

1. Every execution has one execution ID, correlation ID and trace ID.
2. Every capability has a unique stable ID and explicit version.
3. A registry cannot contain duplicate capability IDs.
4. Every emitted event carries execution and capability identity.
5. Capability outputs are immutable once returned.
6. Module-specific metrics do not leak into the platform-core contracts.
