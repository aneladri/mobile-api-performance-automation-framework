#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
PROFILE="${1:-smoke}"
HOST="${JMETER_HOST:-localhost}"
PORT="${JMETER_PORT:-8089}"
PROTOCOL="${JMETER_PROTOCOL:-http}"
THREADS="${JMETER_THREADS:-}"
LOOPS="${JMETER_LOOPS:-}"
RAMP="${JMETER_RAMP_SECONDS:-}"
RESULT_ROOT="$ROOT_DIR/performance/jmeter"
RESULT_FILE="$RESULT_ROOT/results/${PROFILE}.jtl"
REPORT_DIR="$RESULT_ROOT/reports/${PROFILE}"
PLAN="$RESULT_ROOT/plans/api-${PROFILE}.jmx"
JMETER_EXECUTABLE="$($ROOT_DIR/performance/scripts/resolve-jmeter.sh)"

[[ -f "$PLAN" ]] || { echo "Unsupported JMeter profile: $PROFILE. Use smoke or load." >&2; exit 2; }
rm -f "$RESULT_FILE"
rm -rf "$REPORT_DIR"
mkdir -p "$RESULT_ROOT/results" "$REPORT_DIR"

ARGS=(-n -t "$PLAN" -l "$RESULT_FILE" -e -o "$REPORT_DIR" \
  -Jhost="$HOST" -Jport="$PORT" -Jprotocol="$PROTOCOL" \
  -JuserCsv="$ROOT_DIR/performance/jmeter/data/users.csv")
[[ -n "$THREADS" ]] && ARGS+=("-Jthreads=$THREADS")
[[ -n "$LOOPS" ]] && ARGS+=("-Jloops=$LOOPS")
[[ -n "$RAMP" ]] && ARGS+=("-JrampSeconds=$RAMP")

"$JMETER_EXECUTABLE" "${ARGS[@]}"
echo "JMeter binary: $JMETER_EXECUTABLE"
echo "JMeter report: $REPORT_DIR/index.html"
node "$ROOT_DIR/performance/reporting/generate-performance-dashboard.js"
