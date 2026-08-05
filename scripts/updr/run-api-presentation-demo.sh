#!/usr/bin/env bash
set -euo pipefail
ROOT="${1:-$PWD}"; BASE="${UPDR_BASE_URL:-http://localhost:8090}"
if ! curl -fsS "$BASE/health" >/dev/null 2>&1; then bash "$ROOT/scripts/updr/start-updr-demo.sh" "$ROOT"; fi
UPDR_BASE_URL="$BASE" bash "$ROOT/scripts/updr/open-api-monitor.sh"
sleep 2
UPDR_BASE_URL="$BASE" UPDR_API_PACE_MS="${UPDR_API_PACE_MS:-1800}" node "$ROOT/scripts/updr/run-live-api-demo.js"
