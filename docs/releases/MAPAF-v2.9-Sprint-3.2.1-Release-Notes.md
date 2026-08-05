# MAPAF v2.9 Sprint 3.2.1

## Environment Health Probe

### Delivered

- Runtime command execution abstraction
- Process-based command executor
- Java runtime validation
- Gradle wrapper validation
- Python runtime validation
- Node.js validation
- npm validation
- Repository write-access validation
- Required-directory validation
- Doctor HTTP port validation
- Critical versus optional dependency behavior
- Environment health unit tests
- Doctor publisher integration

### Critical dependencies

- Java
- Gradle wrapper
- Python
- Repository write access
- MAPAF source and script directories

A failed critical dependency makes the Environment Health probe
`UNHEALTHY`.

### Optional dependencies

- Node.js
- npm
- Preferred Doctor HTTP port

A failed optional dependency makes the Environment Health probe
`DEGRADED` but does not automatically make the platform unready.
