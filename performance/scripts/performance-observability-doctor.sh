#!/usr/bin/env bash
set -euo pipefail
ROOT="${1:-$(pwd)}"
fail=0
for command in docker curl k6 node; do
  if command -v "$command" >/dev/null 2>&1; then echo "PASS: $command available"; else echo "FAIL: $command unavailable"; fail=1; fi
done
if docker compose version >/dev/null 2>&1; then echo "PASS: docker compose available"; else echo "FAIL: docker compose unavailable"; fail=1; fi
for file in \
  performance/observability/docker-compose.yml \
  performance/observability/prometheus/prometheus.yml \
  performance/observability/opentelemetry/collector-config.yml \
  performance/observability/grafana/provisioning/datasources/prometheus.yml \
  performance/observability/grafana/provisioning/dashboards/dashboards.yml \
  performance/observability/grafana/dashboards/mapaf-k6-performance.json \
  performance/scripts/run-updr-observed-profile.sh; do
  if [ -f "$ROOT/$file" ]; then echo "PASS: $file"; else echo "FAIL: $file"; fail=1; fi
done
node "$ROOT/performance/observability/tests/observability-contract.test.js" "$ROOT"
if [ "$fail" -ne 0 ]; then exit 1; fi
echo "MAPAF Enterprise Performance Observability Doctor passed."
