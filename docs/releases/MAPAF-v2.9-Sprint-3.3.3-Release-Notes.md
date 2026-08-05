# MAPAF v2.9 Sprint 3.3.3

## API Health Probe

### Delivered

- API endpoint configuration validation
- Optional live HTTP connectivity
- HTTP response-status validation
- Response-latency validation
- Response-body validation
- Optional authentication validation
- API enterprise-summary validation
- API dashboard validation
- API failure-showcase validation
- API Health unit tests
- MAPAF Doctor publisher integration

### Live API configuration

Enable live API validation:

`-Dmapaf.api.health.enabled=true`

Configure the endpoint:

`-Dmapaf.api.health.url=http://127.0.0.1:8081/health`

Optional authentication:

`-Dmapaf.api.health.auth.required=true`

Provide the token through:

- `MAPAF_API_TOKEN`
- `API_TOKEN`
- `-Dmapaf.api.health.token`

Configure latency threshold:

`-Dmapaf.api.health.max.latency.ms=2000`

### Status behavior

- Healthy live endpoint and evidence: `HEALTHY`
- Evidence-only mode with valid reports: `HEALTHY`
- High latency or optional evidence gaps: `DEGRADED`
- Missing enterprise summary, failed authentication, unreachable endpoint,
  or invalid HTTP status: `UNHEALTHY`
