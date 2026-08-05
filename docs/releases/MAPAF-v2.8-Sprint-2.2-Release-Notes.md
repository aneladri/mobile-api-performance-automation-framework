# MAPAF v2.8 Sprint 2.2

## Governed Release Gates

### Delivered

- Functional Quality Gate
- API Contract Gate
- Performance SLA Gate
- Quality Gate Context Factory
- Release Readiness Engine integration
- Release-blocking decision enforcement
- `mapaf.release-readiness/v2` contract
- Boundary and failure-path unit tests

### Mandatory policies

#### Functional Quality

- Required functional capabilities must execute
- Success rate must be at least 95%
- Failed critical steps must equal zero

#### API Contract

- Overall API result must pass
- Failed API steps must equal zero
- API P95 response time must not exceed 500 ms

#### Performance SLA

- Performance result must pass
- P95 must not exceed 500 ms
- Error rate must be below 1%
- Availability must be at least 99%

### Release decision

Any failed blocker gate forces:

- `releaseBlocked = true`
- `releaseRisk = HIGH`
- `overallStatus = FAIL`
- `recommendation = DO NOT RELEASE`
