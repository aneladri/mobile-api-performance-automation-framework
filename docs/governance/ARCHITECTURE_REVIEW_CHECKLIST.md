# Architecture Review Checklist

## Product Fit
- [ ] Capability aligns to the approved roadmap.
- [ ] Business outcome and target users are stated.
- [ ] Scope does not repurpose another release milestone.

## Architecture
- [ ] Bounded context and owner are clear.
- [ ] Dependency direction follows Platform Core contracts.
- [ ] No vendor detail leaks into core abstractions.
- [ ] Contract evolution is backward compatible.

## Non-Functional Requirements
- [ ] Security and secret handling are defined.
- [ ] Telemetry and correlation identifiers are defined.
- [ ] Failure, retry, timeout, and degradation behavior are defined.
- [ ] Performance and scalability assumptions are documented.

## Delivery
- [ ] Unit and integration tests exist.
- [ ] Enterprise demo is reproducible.
- [ ] Documentation and release notes are updated.
- [ ] ZIP release and validation results are available.
