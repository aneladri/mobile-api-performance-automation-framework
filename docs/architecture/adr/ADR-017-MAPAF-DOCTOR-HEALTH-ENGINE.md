# ADR-017: MAPAF Doctor Health Engine

## Status

Accepted

## Context

MAPAF requires a standardized way to diagnose whether the platform,
its environment, and its dependencies are capable of producing
trustworthy automation and release decisions.

Health diagnostics must remain independent from:

- test execution;
- release-readiness policies;
- dashboard rendering;
- external provider implementations.

## Decision

MAPAF Doctor is implemented as an independent bounded context with:

- immutable health contracts;
- registered health probes;
- a probe execution engine;
- deterministic health aggregation;
- diagnoses and corrective actions;
- versioned JSON and HTML reports.

Every health capability implements the `HealthProbe` interface.

The Doctor engine orchestrates probes but does not contain
provider-specific health logic.

## Status model

- HEALTHY
- DEGRADED
- UNHEALTHY
- NOT_CONFIGURED

## Severity model

- CRITICAL
- HIGH
- MEDIUM
- LOW
- INFO

## Consequences

New diagnostics can be introduced without modifying the Doctor engine.

Critical unhealthy probes make the platform unready.

Optional and non-critical issues may degrade platform health without
preventing operation.
