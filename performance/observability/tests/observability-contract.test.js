'use strict';
const fs = require('fs');
const path = require('path');
const root = process.argv[2] || process.cwd();
function read(relative) {
  const file = path.join(root, relative);
  if (!fs.existsSync(file)) throw new Error(`Missing required file: ${relative}`);
  return fs.readFileSync(file, 'utf8');
}
const compose = read('performance/observability/docker-compose.yml');
for (const service of ['prometheus:', 'grafana:', 'otel-collector:']) {
  if (!compose.includes(service)) throw new Error(`Missing service ${service}`);
}
if (!compose.includes('--web.enable-remote-write-receiver')) throw new Error('Prometheus remote-write receiver is not enabled');
const dashboard = JSON.parse(read('performance/observability/grafana/dashboards/mapaf-k6-performance.json'));
if (dashboard.uid !== 'mapaf-k6-performance') throw new Error('Unexpected Grafana dashboard UID');
if (!Array.isArray(dashboard.panels) || dashboard.panels.length < 6) throw new Error('Grafana dashboard must expose enterprise KPI panels');
const runner = read('performance/scripts/run-updr-observed-profile.sh');
if (!runner.includes('experimental-prometheus-rw')) throw new Error('Observed runner does not publish k6 metrics to Prometheus');
if (!runner.includes('K6_PROMETHEUS_RW_TREND_STATS')) throw new Error('Trend percentile export is not configured');
const collector = read('performance/observability/opentelemetry/collector-config.yml');
if (!collector.includes('otlp:') || !collector.includes('prometheus:')) throw new Error('OpenTelemetry collector pipelines are incomplete');
console.log('MAPAF enterprise performance observability contract tests passed.');
