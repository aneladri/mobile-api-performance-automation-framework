#!/usr/bin/env bash
set -euo pipefail
ROOT="${1:-$(pwd)}"
fail=0
check_cmd(){ if command -v "$1" >/dev/null 2>&1; then echo "PASS: $1 available"; else echo "FAIL: $1 unavailable"; fail=1; fi; }
check_file(){ if [ -f "$ROOT/$1" ]; then echo "PASS: $1"; else echo "FAIL: $1"; fail=1; fi; }
check_cmd node
check_cmd k6
check_cmd curl
check_file performance/k6/platform/workloads/updr-smoke.js
check_file performance/k6/platform/workloads/updr-load.js
check_file performance/k6/platform/workloads/updr-spike.js
check_file performance/k6/platform/workloads/updr-ten-thousand.js
check_file performance/k6/platform/thresholds/updr.js
check_file performance/k6/platform/reporting/normalize-k6-summary.js
BASE_URL="${UPDR_BASE_URL:-http://localhost:8090}"
if curl -fsS "$BASE_URL/health" >/dev/null 2>&1; then echo "PASS: UPDR service reachable at $BASE_URL"; else echo "WARN: UPDR service is not reachable at $BASE_URL"; fi
if [ "$fail" -ne 0 ]; then exit 1; fi
echo "MAPAF k6 Performance Doctor passed."
