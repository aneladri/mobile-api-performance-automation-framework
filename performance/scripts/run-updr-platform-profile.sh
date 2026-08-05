#!/usr/bin/env bash
set -euo pipefail
ROOT="${1:-$(pwd)}"
PROFILE="${2:-smoke}"
REPORT_ROOT="$ROOT/performance/updr/reports/platform/$PROFILE"
mkdir -p "$REPORT_ROOT"

case "$PROFILE" in
  smoke) SCRIPT="performance/k6/platform/workloads/updr-smoke.js" ;;
  load) SCRIPT="performance/k6/platform/workloads/updr-load.js" ;;
  spike) SCRIPT="performance/k6/platform/workloads/updr-spike.js" ;;
  ten-thousand) SCRIPT="performance/k6/platform/workloads/updr-ten-thousand.js" ;;
  *) echo "Unsupported profile: $PROFILE" >&2; exit 2 ;;
esac

RAW="$REPORT_ROOT/k6-summary.json"
cd "$ROOT"
k6 run --summary-export="$RAW" "$SCRIPT"
node performance/k6/platform/reporting/normalize-k6-summary.js "$RAW" "$REPORT_ROOT" "$PROFILE"
