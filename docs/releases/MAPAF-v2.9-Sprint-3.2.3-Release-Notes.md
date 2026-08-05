# MAPAF v2.9 Sprint 3.2.3

## Dashboard Health Probe

### Delivered

- Dashboard inspection abstraction
- Filesystem dashboard inspector
- Command Center availability validation
- Executive Summary validation
- Release Readiness HTML and JSON validation
- MAPAF Doctor HTML and JSON validation
- Allure Evidence Hub validation
- Mobile, Web, API, and Performance dashboard validation
- Release Readiness schema validation
- Executive Decision schema validation
- MAPAF Doctor schema validation
- Internal relative-link validation
- Optional HTTP reachability validation
- Dashboard Health unit tests
- MAPAF Doctor publisher integration

### Critical dashboard dependencies

- Command Center
- Executive Summary
- Release Readiness HTML
- Release Readiness JSON
- MAPAF Doctor HTML
- MAPAF Doctor JSON
- Supported contract versions

A failed critical dashboard dependency reports `UNHEALTHY`.

### Advisory dashboard dependencies

- Allure Evidence Hub
- Mobile report
- Web report
- API report
- Performance report
- Internal dashboard links
- Optional HTTP serving

A failed advisory dependency reports `DEGRADED`.
