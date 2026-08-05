#!/usr/bin/env bash
set -euo pipefail
ROOT="${1:-$(pwd)}"
PROFILE="${2:-load}"
REPORT_ROOT="$ROOT/performance/updr/reports/platform/$PROFILE"
mkdir -p "$REPORT_ROOT"
case "$PROFILE" in
  smoke) SCRIPT="performance/k6/platform/workloads/updr-smoke.js" ;;
  load) SCRIPT="performance/k6/platform/workloads/updr-load.js" ;;
  spike) SCRIPT="performance/k6/platform/workloads/updr-spike.js" ;;
  ten-thousand) SCRIPT="performance/k6/platform/workloads/updr-ten-thousand.js" ;;
  *) echo "Unsupported profile: $PROFILE" >&2; exit 2 ;;
esac
PROM_PORT="${MAPAF_PROMETHEUS_PORT:-9090}"
export K6_PROMETHEUS_RW_SERVER_URL="${K6_PROMETHEUS_RW_SERVER_URL:-http://localhost:${PROM_PORT}/api/v1/write}"
export K6_PROMETHEUS_RW_TREND_STATS="${K6_PROMETHEUS_RW_TREND_STATS:-p(90),p(95),p(99),avg,min,max}"
export K6_PROMETHEUS_RW_STALE_MARKERS="${K6_PROMETHEUS_RW_STALE_MARKERS:-true}"
RAW="$REPORT_ROOT/k6-summary.json"
cd "$ROOT"
k6 run -o experimental-prometheus-rw --summary-export="$RAW" "$SCRIPT"
node performance/k6/platform/reporting/normalize-k6-summary.js "$RAW" "$REPORT_ROOT" "$PROFILE"
echo "Grafana dashboard: http://localhost:${MAPAF_GRAFANA_PORT:-3000}/d/mapaf-k6-performance"
