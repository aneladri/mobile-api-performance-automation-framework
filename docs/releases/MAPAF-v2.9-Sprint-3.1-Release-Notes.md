# MAPAF v2.9 Sprint 3.1

## MAPAF Doctor Foundation

### Delivered

- Health status model
- Health severity model
- Diagnostic check contract
- Health probe definition
- Health probe result contract
- Doctor context
- Health probe interface
- In-memory probe registry
- Doctor assessment engine
- Versioned `mapaf.doctor/v1` snapshot
- Initial JSON report
- Initial HTML report
- Repository Foundation probe
- Product Version probe
- Doctor foundation unit tests
- Architecture decision record
- Architecture diagram
- Static release validation

### Health outcomes

- HEALTHY
- DEGRADED
- UNHEALTHY
- NOT_CONFIGURED

### Platform readiness policy

- A critical unhealthy probe makes MAPAF unready.
- Non-critical unhealthy probes degrade MAPAF but do not block operation.
- Optional unconfigured capabilities do not automatically make MAPAF unhealthy.

### Foundation probes

#### Repository Foundation

Validates:

- `build.gradle`
- `settings.gradle`
- `gradlew`
- `MAPAF_VERSION`

#### Product Version

Validates that the active runtime version matches the value stored in `MAPAF_VERSION`.

### Contract

- `mapaf.doctor/v1`

### Sprint outcome

MAPAF Doctor now has an extensible health-engine foundation. Environment, browser, dashboard, Claude, device, API, and performance probes can be added without modifying the Doctor engine.
