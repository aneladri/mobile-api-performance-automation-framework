# MAPAF v2.9 Sprint 3.4.3

## Doctor CLI and Executive Report

### Delivered

- Root-level `mapaf-doctor` command
- Executive console status
- `run`, `status`, `report`, `json`, and `open` commands
- `--no-refresh`, `--strict`, and `--quiet` options
- Executive report JSON contract: `mapaf.doctor.executive-report/v1`
- Markdown executive report
- HTML executive report
- Dependency-free PDF executive report
- Gradle report task
- CLI regression test
- Static validation gate
- Patch installation, validation, and rollback support

### Generated artifacts

- `dashboard/reports/doctor/executive-report.json`
- `dashboard/reports/doctor/executive-report.md`
- `dashboard/reports/doctor/executive-report.html`
- `dashboard/reports/doctor/executive-report.pdf`

### Usage

```bash
./mapaf-doctor
./mapaf-doctor status --no-refresh
./mapaf-doctor report --no-refresh
./mapaf-doctor json --no-refresh
./mapaf-doctor open --no-refresh
```
