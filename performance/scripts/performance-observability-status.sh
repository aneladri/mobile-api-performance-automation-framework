#!/usr/bin/env bash
set -euo pipefail
ROOT="${1:-$(pwd)}"
PROM_PORT="${MAPAF_PROMETHEUS_PORT:-9090}"
GRAFANA_PORT="${MAPAF_GRAFANA_PORT:-3000}"
OTEL_PORT="${MAPAF_OTEL_HTTP_PORT:-4318}"
wait_for() {
  local name="$1" url="$2" attempts="${3:-30}"
  for _ in $(seq 1 "$attempts"); do
    if curl -fsS "$url" >/dev/null 2>&1; then
      echo "PASS: $name reachable at $url"
      return 0
    fi
    sleep 1
  done
  echo "FAIL: $name not reachable at $url" >&2
  return 1
}
wait_for "Prometheus" "http://localhost:${PROM_PORT}/-/ready" 30
wait_for "Grafana" "http://localhost:${GRAFANA_PORT}/api/health" 45
# OTLP/HTTP root may return 404; validate TCP listener through curl response instead.
if curl -sS -o /dev/null "http://localhost:${OTEL_PORT}/"; then
  echo "PASS: OpenTelemetry HTTP receiver reachable on port ${OTEL_PORT}"
else
  echo "WARN: OpenTelemetry receiver did not answer on port ${OTEL_PORT}" >&2
fi
echo "Grafana: http://localhost:${GRAFANA_PORT}/d/mapaf-k6-performance"
echo "Prometheus: http://localhost:${PROM_PORT}"
