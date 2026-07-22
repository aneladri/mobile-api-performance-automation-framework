#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
BASE_URL="${BASE_URL:-http://localhost:8089}"
PROFILE="${1:-smoke}"
RESULT_DIR="$ROOT_DIR/performance/results/k6"
REPORT_DIR="$ROOT_DIR/performance/k6/reports/$PROFILE"
SUMMARY_FILE="$RESULT_DIR/${PROFILE}-summary.json"
REPORT_FILE="$REPORT_DIR/index.html"
mkdir -p "$RESULT_DIR" "$REPORT_DIR"
command -v k6 >/dev/null 2>&1 || { echo "k6 is not installed or not on PATH." >&2; exit 127; }
command -v node >/dev/null 2>&1 || { echo "Node.js is required to generate the k6 HTML report." >&2; exit 127; }
case "$PROFILE" in
  smoke) SCRIPT="$ROOT_DIR/performance/k6/smoke/api-smoke.js" ;;
  load) SCRIPT="$ROOT_DIR/performance/k6/load/api-load.js" ;;
  stress) SCRIPT="$ROOT_DIR/performance/k6/stress/api-stress.js" ;;
  spike) SCRIPT="$ROOT_DIR/performance/k6/spike/api-spike.js" ;;
  soak) SCRIPT="$ROOT_DIR/performance/k6/soak/api-soak.js" ;;
  *) echo "Usage: $0 [smoke|load|stress|spike|soak]" >&2; exit 2 ;;
esac

rm -f "$SUMMARY_FILE"
rm -rf "$REPORT_DIR"
mkdir -p "$REPORT_DIR"

set +e
BASE_URL="$BASE_URL" k6 run --summary-export="$SUMMARY_FILE" "$SCRIPT"
K6_EXIT_CODE=$?
set -e

if [[ -f "$SUMMARY_FILE" ]]; then
  node "$ROOT_DIR/performance/reporting/generate-k6-html.js" \
    "$SUMMARY_FILE" "$REPORT_FILE" "$PROFILE" "$K6_EXIT_CODE"
  node "$ROOT_DIR/performance/reporting/generate-performance-dashboard.js"
else
  echo "k6 did not create the expected summary file: $SUMMARY_FILE" >&2
fi

echo "k6 summary: $SUMMARY_FILE"
echo "k6 HTML report: $REPORT_FILE"
exit "$K6_EXIT_CODE"
