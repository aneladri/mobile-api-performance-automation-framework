# MAPAF Doctor Executive Experience

```text
Health Probes
     |
     v
DefaultDoctorEngine
     |
     v
DoctorSnapshot
mapaf.doctor/v1
     |
     +-------------------------+
     |                         |
     v                         v
Detailed Doctor Report    DoctorExecutiveMapper
                              |
                              v
                    DoctorExecutiveOverview
                    mapaf.doctor.executive/v1
                              |
                        +-----+-----+
                        |           |
                        v           v
                doctor-overview  doctor-overview
                     .json           .html

## 13. Add release notes

```bash
cat > docs/releases/MAPAF-v2.9-Sprint-3.4.1-Release-Notes.md <<'EOF'
# MAPAF v2.9 Sprint 3.4.1

## Executive Doctor Dashboard

### Delivered

- Executive Doctor summary contract
- `mapaf.doctor.executive/v1`
- Weighted platform health score
- Probe-specific weighting model
- Capability readiness summary
- Health distribution
- Blocking issue count
- Warning count
- Priority issue summary
- Recommended actions summary
- Executive HTML dashboard
- Executive JSON dashboard
- Navigation to Doctor details and evidence reports
- Executive dashboard unit tests
- Executive publisher tests
- Architecture documentation
- Static validation gate

### Weighted health model

- Repository Foundation: 15
- Product Version: 5
- Environment Health: 15
- Browser Health: 10
- Dashboard Health: 10
- Claude Health: 5
- Device Health: 10
- API Health: 15
- Performance Health: 15

The detailed Doctor contract remains `mapaf.doctor/v1`.
