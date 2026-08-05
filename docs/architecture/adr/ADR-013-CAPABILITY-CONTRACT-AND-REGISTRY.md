# ADR-013: Standardize capabilities behind a registry contract

**Status: Accepted**

## Decision
Every future executable platform extension exposes a stable capability descriptor and implements the `Capability` contract. Duplicate IDs are rejected.

## Consequences
Discovery and execution become uniform. Module-specific runtime objects remain behind adapters.
