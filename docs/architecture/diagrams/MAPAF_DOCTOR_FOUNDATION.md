# MAPAF Doctor Foundation

```text
Repository and Runtime Environment
                 |
                 v
           DoctorContext
                 |
                 v
       HealthProbeRegistry
                 |
       +---------+----------+
       |                    |
       v                    v
Repository Probe      Product Version Probe
       |                    |
       +---------+----------+
                 |
                 v
        DefaultDoctorEngine
                 |
                 v
          DoctorSnapshot
       mapaf.doctor/v1
                 |
          +------+------+
          |             |
          v             v
 doctor-report.json  doctor-report.html

## 22. Release notes

```bash
cat > docs/releases/MAPAF-v2.9-Sprint-3.1-Release-Notes.md <<'EOF'
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
