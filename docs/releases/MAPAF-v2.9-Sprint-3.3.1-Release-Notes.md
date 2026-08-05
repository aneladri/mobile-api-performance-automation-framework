# MAPAF v2.9 Sprint 3.3.1

## Claude Health Probe

### Delivered

- Claude provider configuration validation
- API credential validation
- Provider endpoint validation
- Optional connectivity-check contract
- Governed AI-generation replay validation
- Generated automation asset inventory
- Live-provider and governed-replay execution modes
- Claude Health unit tests
- Doctor publisher integration

### Health behavior

- Healthy live provider: `HEALTHY`
- Live provider unavailable with governed replay: `DEGRADED`
- Provider disabled with no replay assets: `NOT_CONFIGURED`
- Enabled provider failure with no replay: `UNHEALTHY`

Claude remains non-blocking when governed replay assets are available.
